
class Trace(object):
	"""docstring for ClassName"""
	def __init__(self):
		self._consumption = 0
		self._time = 0
		self._classification = 0
		self._trace = []

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
		