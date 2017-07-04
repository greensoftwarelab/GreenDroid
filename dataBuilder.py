#!/usr/bin/python

import re, sys, os
from lazyme.string import color_print
from pandas import *

#from gd_analysis.app_data import AppData 
#from gd_analysis.trace import Trace
from gd_analysis import *

TAG="[DATA BUILDER]"

def main(mf):
	color_print(TAG, color='green', bold=True)
	#for c in mf:
	all_apps_path = childDirs(mf)
	all_apps = []
	all_energy = []
	for path in all_apps_path:
		app = AppData(path, mf)
		all_apps.append(app)
		all_energy += app.consumptions_over_trace()
	print(str(all_energy))

if __name__ == "__main__":
	if len(sys.argv) > 1:
		main_folder = sys.argv[1]
		main(main_folder)

# interesting app: 059ffbed-ae13-4eea-924c-78d27c5ef448