#!/usr/bin/python

import os, re, csv

def childDirs(path):
	dirs = next(os.walk(path))[1]
	return list(map(lambda x : os.path.join(path, str(x)), dirs))

def filesByTypeInDir(path, suffix, name_only=False):
	lst = []
	name = ""
	for root, dirs, files in os.walk(path):
		for file in files:
			if file.endswith(suffix):
				if name_only:
					name = re.sub(path, '', os.path.join(root, file))
				else:
					name = os.path.join(root, file)
				lst.append(name)
	return lst

def filterFilesInDir(path, preffix, suffix, name_only=False):
	lst = []
	name = ""
	for root, dirs, files in os.walk(path):
		for file in files:
			if (file.startswith(preffix)) & (file.endswith(suffix)):
				if name_only:
					name = re.sub(path, '', os.path.join(root, file))
				else:
					name = os.path.join(root, file)
				lst.append(name)
	return lst

def load_consumptions(path):
	firstline = True
	result = []
	file = os.path.join(path, 'Testresults.csv')
	with open(file) as csvfile:
		spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
		for row in spamreader:
			if firstline:
				firstline = False
				continue
			if (is_number(row[0])) & (len(row) >= 5):
				result.append({'test' : row[0], 'energy' : row[1], 'power' : row[2], 'time' : row[3], 'coverage' : row[4]})
	return result
	
def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

def obj_dict(obj):
    return obj.__dict__