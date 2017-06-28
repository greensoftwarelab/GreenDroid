#!/usr/bin/python

import re, sys, os
from gd_analysis.app_data import AppData 
from gd_analysis.trace import Trace
from lazyme.string import color_print

TAG="[DATA BUILDER]"

def childDirs(path):
	dirs = next(os.walk(path))[1]
	return list(map(lambda x : os.path.join(path, str(x)), dirs))

def filesByTypeInDir(path, suffix):
	lst = []
	for root, dirs, files in os.walk(path):
		for file in files:
			if file.endswith(suffix):
				lst.append(re.sub(path, '', os.path.join(root, file)))
	return lst

def main(mf):
	color_print(TAG, color='green', bold=True)
	#for c in mf:
	all_apps_path = childDirs(mf)
	for path in all_apps_path:
		app = AppData(path, mf)
		app.init_packages()

if __name__ == "__main__":
	if len(sys.argv) > 1:
		main_folder = sys.argv[1]
		main(main_folder)