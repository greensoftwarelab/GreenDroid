#!/usr/bin/python

class TestCase(object):
	"""docstring for ClassName"""
	def __init__(self):
		self._index = -1
		self._consumption = 0
		self._time = 0
		self._classification = ""
		self._trace = []

	def __init__(self, index, consumption, time, trace):
		self._index = index
		self._consumption = consumption
		self._time = time
		self._classification = ""
		self._trace = trace

	#get methods
	def get_consumption(self):
		return self._consumption

	def get_time(self):
		return self._time

	def get_classification(self):
		return self._classification

	def get_trace(self):
		return self._trace

	# set methods
	def set_consumption(self, consumption):
		self._consumption = cons

	def set_time(self, time):
		self._time = time

	def set_classification(self, classif):
		self._classification = classif

	def set_trace(self, trace):
		self._trace = trace
	
	def classify(self, quantiles, method=""):
		if method == "consumptions_over_time":
			value = self._consumption/self_time
		elif method == "consumptions_over_trace":
			value = self._consumption/len(self._trace)
		elif method == "consumptions_over_time_over_trace":
			value = self._consumption/self._time/len(self._trace)
		elif method == "consumptions_on_time":
			value = self._consumption*self._time
		elif method == "consumptions_on_trace":
			value = self._consumption*len(self._trace)
		elif method == "consumptions_on_time_on_trace":
			value = self._consumption*self._time*len(*self._trace)
		else:	# method == ""
			value = self._consumption
		
		lowest_key, lowest_val, diff = "", 9999999, 9999999;
		classification = ""
		for key, val in quantiles.items():
			if (val <= value) & ((value - val) < diff):
				diff = value - val
				classification = key
			if val < lowest_val:
				lowest_val = val
				lowest_key = key

		if classification == "":
			classification = quantiles[lowest_key]

		self._classification = classification
		return self._classification


	def __str__(self):
		return "Test Case " + str(self._index) + ": \n\t[Energy] " + str(self._consumption) + "\n\t[Time] " + str(self._time) + "\n\t[Trace] " + str(self._trace) # + "\n\n\t::RANK:: " + str(self._classification)