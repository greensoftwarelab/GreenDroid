#!/usr/bin/python

import os, re
from .test_case import *
from .util import *

class AppData(object):
	"""docstring for ClassName"""
	def __init__(self, path, ref):
		self._path = path
		self._test_cases = []
		self._packages = {'id' : ref, 'packages': []}
		self._id = ref
		self._load_packages()
		self._load_test_cases()
	
	# get methods
	def get_test_cases(self):
		return self._test_cases

	def get_classes(self):
		return self._classes

	def get_id(self):
		return self._id

	def get_packages(self):
		return self._packages

	# set methods
	def set_test_cases(self, traces):
		self._test_cases = traces

	def set_classes(self, classes):
		self._classes = classes

	def set_id(self, ref):
		self._id = ref

	#auxiliar methods
	def get_all_consumptions(self):
		res = []
		for t in self._test_cases:
			res.append(t.get_consumption())
		return res

	def get_all_times(self):
		res = []
		for t in self._test_cases:
			res.append(t.get_time())
		return res

	def get_all_traces(self):
		res = []
		for t in self._test_cases:
			res.append(t.get_traces())
		return res

	def consumptions_over_time(self):
		res = []
		for t in self._test_cases:
			res.append((t.get_consumption())/t.get_time())
		return res

	def consumptions_over_trace(self):
		res = []
		for t in self._test_cases:
			if len(t.get_trace()) > 0:
				size = len(t.get_trace())
			else:
				size = 1
			res.append((t.get_consumption())/size)
		return res

	def consumptions_over_time_over_trace(self):
		res = []
		for t in self._test_cases:
			if len(t.get_trace()) > 0:
				size = len(t.get_trace())
			else:
				size = 1
			res.append((t.get_consumption())/t.get_time()/size)
		return res

	def consumptions_on_time(self):
		res = []
		for t in self._test_cases:
			res.append((t.get_consumption())*t.get_time())
		return res

	def consumptions_on_trace(self):
		res = []
		for t in self._test_cases:
			if len(t.get_trace()) > 0:
				size = len(t.get_trace())
			else:
				size = 1
			res.append((t.get_consumption())*size)
		return res

	def consumptions_on_time_on_trace(self):
		res = []
		for t in self._test_cases:
			if len(t.get_trace()) > 0:
				size = len(t.get_trace())
			else:
				size = 1
			res.append((t.get_consumption())*t.get_time()*size)
		return res

	#auxiliar private methods
	def _load_packages(self):
		file = os.path.join(self._path, "all/allMethods.txt")
		self._packages['packages'] = self._load_trace_file(file, False)

	def _load_trace_file(self, file, keep_count=True):
		ret = []
		with open(file) as fp:
			content = fp.readlines()
			for line in content:
				aux = re.sub(r'>|\n', '', line).split('<')
				package, class_name = aux[0].rsplit('.',1)
				method = aux[1]
				
				filtered_pkg = [pkg for pkg in ret if pkg['name'] == package]
				if len(filtered_pkg) > 0:
					filtered_cls = [cl for cl in filtered_pkg[0]['classes'] if cl['name'] == class_name]
					if len(filtered_cls) > 0:
						filtered_mts = [m for m in filtered_cls[0]['methods'] if m['name'] == method]
						if len(filtered_mts) == 0:
							filtered_cls[0]['methods'].append({'name': method, 'count' : 1})
						elif keep_count:
							filtered_mts[0]['count']+=1
					else:
						filtered_pkg[0]['classes'].append({'name' : class_name, 'methods' : [{'name' : method, 'count' : 1}]})
				else:
					ret.append({'name' : package, 'classes' : [{'name' : class_name, 'methods' : [{'name' : method, 'count' : 1}]}]})
		return ret

	def _load_test_cases(self):
		files = filterFilesInDir(self._path, "TracedMethods", ".txt")
		results = load_consumptions(self._path)
		for f in files:
			number = re.sub(r'\/TracedMethods|\.txt|'+self._path, r'', f)
			values = [r for r in results if r['test'] == number]
			if len(values):
				v = values[0]
				energy = float(v['energy'])
				time = v['time']
				trace = self._load_trace_file(f)
				if len(trace) == 0:
					print("NULL TRACE: " + str(number))
				tc = TestCase(number, energy, time, trace)
				self._test_cases.append(tc)