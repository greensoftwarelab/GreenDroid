#!/usr/bin/python

import re, sys, os

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

def main(mf):
	color_print(TAG, color='green', bold=True)
	
	all_apps_path = childDirs(mf)
	all_apps = []
	all_energy = []
	for path in all_apps_path:
		print(path)
		#run Analyzer
		run_analyzer(path)
      	#get the results & store it in classes
		app = AppData(path, mf)
		all_apps.append(app)
		all_energy += app.consumptions_over_trace()	#other options available
	
	print(str(all_energy))
	print("Calculating the quantiles")
	df = DataFrame(Series(all_energy))
	quantiles = df.quantile(np.linspace(.1, 1, num=10, endpoint=True))
	#quantiles[.1][0] <= the value of quantile 0.1, for column 0 (first one)
	red_test = quantiles[.9][0]
	yellow_test = quantiles[.8][0]
	green_test = quantiles[.7][0]
	
	quantiles_dict = {'green': green_test, 'yellow' : yellow_test, 'red' : red_test}
	print(str(quantiles_dict))

if __name__ == "__main__":
	if len(sys.argv) > 1:
		main_folder = sys.argv[1]
		main(main_folder)

# interesting app: 059ffbed-ae13-4eea-924c-78d27c5ef448