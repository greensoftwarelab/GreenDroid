from rest_framework import serializers
from repoApp.models.testRelated import *
from django.utils import timezone
from repoApp.serializers.metricRelatedSerializers import TestMetricSerializer
from repoApp.models.metricsRelated import TestMetric

class TestListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        tm = [Test(**item) for item in validated_data]
        return Test.objects.bulk_create(tm)

class TestSerializer(serializers.ModelSerializer):
    test_orientation = serializers.PrimaryKeyRelatedField(queryset=TestOrientation.objects.all())
    class Meta:
        model = Test
        list_serializer_class = TestListSerializer
        fields = ('id','test_application', 'test_tool', 'test_orientation')
        validators = []

class TestResultsListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        tm = [TestResults(**item) for item in validated_data]
        return TestResults.objects.bulk_create(tm)



class TestResultsSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestResults
        list_serializer_class = TestResultsListSerializer
        fields = ('test_results_id' ,'test_results_timestamp', 'test_results_seed', 'test_results_description', 'test_results_test', 'test_results_profiler','test_results_device', 'test_results_device_begin_state','test_results_device_end_state')

class TestResultsFullSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(TestResultsFullSerializer, self).__init__(*args, **kwargs)
        
        self.fields['test_metrics'] = serializers.SerializerMethodField()
     
    def get_test_metrics(self, test):
        met = TestMetric.objects.filter(test_results__in=test.test_results)
        return TestMetricSerializer(instance=met,  many=True).data  
    class Meta:
        model = TestResults
        fields = ('test_results_id', 'test_results_timestamp', 'test_results_seed', 'test_results_description', 'test_results_test', 'test_results_profiler','test_results_device', 'test_results_device_begin_state','test_results_device_end_state')



class TestOrientationSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestOrientation
        fields = ('test_orientation_id', 'test_orientation_designation')


class ToolSerializer(serializers.ModelSerializer):
    class Meta:
        model = Tool
        fields = ('tool_name')

class MethodInvokedSerializer(serializers.ModelSerializer):
    class Meta:
        model = MethodInvoked
        fields = ('method','test_results')
        validators = []

class ProfilerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Profiler
        fields = ('profiler_name', 'profiler_type')

class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = ('device_serial_number','device_brand','device_model')
        validators = []

class DeviceStateSerializer(serializers.ModelSerializer):
    class Meta:
        model = DeviceState
        fields = ('device_state_id', 'device_state_mem', 'device_state_cpu_free','device_state_cpu_free','device_state_nr_processes_running','device_state_api_level','device_state_android_version')
