#!/usr/bin/python

import os, re
from trace import Trace

class AppData(object):
	"""docstring for ClassName"""
	def __init__(self, path, ref):
		self._path = path
		self._traces = []
		self._packages = {'id' : ref, 'packages': []}
		self._id = ref
		#_init_packages()
		#_get_traces()
	
	# get methods
	def get_traces(self):
		return self._traces

	def get_classes(self):
		return self._classes

	def get_id(self):
		return self._id

	# set methods
	def set_traces(self, traces):
		self._traces = traces

	def set_classes(self, classes):
		self._classes = classes

	def set_id(self, ref):
		self._id = ref

	def _init_packages(self):
		file = os.path.join(self._path, "all/allMethods.txt")
		with open(file) as fp:
			content = fp.readlines()
			#print(str(self._package['name']))
			for line in content:
				aux = re.sub(r'>|\n', '', line).split('<')
				package, class_name = aux[0].rsplit('.',1)
				method = aux[1]
				
				filtered_pkg = [pkg for pkg in self._packages['packages'] if pkg['name'] == package]
				if len(filtered_pkg) > 0:
					filtered_cls = [cl for cl in filtered_pkg[0]['classes'] if cl['name'] == class_name]
					if len(filtered_cls) > 0:
						if method not in filtered_cls[0]['methods']:
							filtered_cls[0]['methods'].append(method)
					else:
						filtered_pkg[0]['classes'].append({'name' : class_name, 'methods' : [method]})
				else:
					self._packages['packages'].append({'name' : package, 'classes' : [{'name' : class_name, 'methods' : [method]}]})
		print(str(self._packages))
		return file

	def init_packages(self):
		self._init_packages()