#!/usr/bin/python

import re, sys, os
import statistics as st

from subprocess import call, check_output, Popen, PIPE
from lazyme.string import color_print
from pandas import *

#from gd_analysis.app_data import AppData 
#from gd_analysis.trace import Trace
from gd_analysis import *

TAG="[DATA BUILDER]"

def run_analyzer(path):
	cmd = "java -jar analyzer/Analyzer-1.0-SNAPSHOT.jar -TraceMethods " + path + " " + path + "/all/ " + path +"/*.csv"
	pipes = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE)
	std_out, std_err = pipes.communicate()
	if pipes.returncode != 0:
		err_msg = "%s. Code: %s" % (std_err.strip(), pipes.returncode)
		color_print('[E] Error on ' + root + ': ', color='red', bold=True)
		print(err_msg)

def compute_quantiles(all_energy):
	df = DataFrame(Series(all_energy))
	quantiles = df.quantile(np.linspace(.1, 1, num=10, endpoint=True))
	quantiles.index = np.round(quantiles.index, decimals=1)
	print(str(quantiles))
	red_test = quantiles.loc[0.9][0]
	yellow_test = quantiles.loc[0.8][0]
	green_test = quantiles.loc[0.7][0]
	return red_test, yellow_test, green_test

def compute_test_stats(all_apps, minimum_number_tests=0):
	tests = []
	cov = []
	for a in all_apps:
		if a.number_of_tests() >= minimum_number_tests:
			num = a.number_of_tests()
			cov += a.total_test_coverage()
			tests.append(num)
		
	return tests, cov

def compute_test_info(all_apps_path, print_status=True, minimum_number_tests=0):
	all_apps = []
	all_energy = []
	count, i = 0, 0
	if print_status:
		perc = [
		{"percentage" : "0" , "value" : 0}, 
		{"percentage" : "20", "value" : len(all_apps_path)*0.20}, 
		{"percentage" : "40", "value" : len(all_apps_path)*0.40}, 
		{"percentage" : "60", "value" : len(all_apps_path)*0.60}, 
		{"percentage" : "80", "value" : len(all_apps_path)*0.80}, 
		{"percentage" : "100", "value" : len(all_apps_path)}
		]
	for path in all_apps_path:
		id = re.sub(r'.+\/(.+)', r'\1', path)
		if (print_status) & (count >= perc[i]["value"]):
			print(str(perc[i]["percentage"]) + "% analyzed")
			i+=1
		#run Analyzer
		run_analyzer(path)
      	#get the results & store it in classes
		app = AppData(path, id)
		if (app.all_OK()) & (app.number_of_tests() >= minimum_number_tests):
			all_apps.append(app)
			all_energy += app.consumptions_over_trace()	#other options available
		count+=1
	return all_apps, all_energy

def main(mf):
	minimum = 1

	color_print(TAG, color='green', bold=True)
	
	all_apps_path = childDirs(mf)
	all_apps, all_energy = compute_test_info(all_apps_path, minimum_number_tests=minimum)
	
	
	color_print("Calculating the quantiles", color="green", bold=True)
	red, yellow, green = compute_quantiles(all_energy)
	print("Quantiles: ")
	print("\tRed (0.9) - " + str(red))
	print("\tYellow (0.8) - " + str(yellow))
	print("\tGreen (0.7) - " + str(green))

	color_print("Tests per App", color="green", bold=True)
	tests, cov = compute_test_stats(all_apps)
	#for n in tests:
	#	print("-> " + str(n))
	print("#Apps with >= " + str(minimum) + " tests: " + str(len(all_apps)))

	print("Average #Tests per App: " + str(st.mean(tests)))

	print("Average Test Coverage (Global): " + str(st.mean(cov)))


	#quantiles_dict = {'green': green_test, 'yellow' : yellow_test, 'red' : red_test}
	#print(str(quantiles_dict))

if __name__ == "__main__":
	if len(sys.argv) > 1:
		main_folder = sys.argv[1]
		main(main_folder)

# interesting app: 
# 059ffbed-ae13-4eea-924c-78d27c5ef448 
