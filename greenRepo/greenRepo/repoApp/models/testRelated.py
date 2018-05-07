from django.db import models
from django.utils.timezone import now
from enumfields import EnumField
from enumfields import Enum  # Uses Ethan Furman's "enum34" backport
from repoApp.models.appRelated import *



class TestOrientation(models.Model):
    #test_orientation_id = models.AutoField(primary_key=True)
    def save(self, *args, **kwargs):
        self.test_orientation_designation = self.test_orientation_designation.lower()
        return super(TestOrientation, self).save(*args, **kwargs)
    test_orientation_designation = models.CharField(max_length=20,primary_key=True)

# The testing tool/framework used to test the application
class Tool(models.Model):
    tool_name = models.CharField(max_length=70, primary_key=True)
    def save(self, *args, **kwargs):
        self.tool_name = self.tool_name.lower()
        return super(Tool, self).save(*args, **kwargs)


class ProfilerType(Enum):
    HARDWARE_BASED = 'h'
    MODEL_BASED = 'm'
    SOFTWARE_BASED = 's'

class Profiler(models.Model):
    profiler_name = models.CharField(max_length=70, primary_key=True)
    profiler_type = EnumField(ProfilerType, max_length=1)
    profiler_version = models.FloatField(default=1.0)
    def save(self, *args, **kwargs):
        self.profiler_name = self.profiler_name.lower()
        return super(Profiler, self).save(*args, **kwargs)

#m = Profiler.objects.filter(profiler_type=ProfilerType.HARDWARE_BASED)

class Device(models.Model):
    device_serial_number = models.CharField(max_length=20,primary_key=True)
    device_brand = models.CharField(max_length=20)
    device_model = models.CharField(max_length=20)
    def save(self, *args, **kwargs):
        self.device_brand = self.device_brand.lower().replace(" ", "")
        self.device_model = self.device_model.lower().replace(" ", "")
        return super(Device, self).save(*args, **kwargs)

class DeviceState(models.Model):
    device_state_id = models.AutoField(primary_key=True)
    device_state_mem = models.IntegerField()
    device_state_cpu_free = models.IntegerField()
    device_state_nr_processes_running = models.IntegerField()
    device_state_api_level = models.IntegerField()
    device_state_android_version = models.FloatField()


class Test(models.Model):
    class Meta:
        unique_together = (('test_application', 'test_tool','test_orientation'),)
    test_application = models.ForeignKey(Application, related_name='tested_app', on_delete=models.CASCADE)
    test_tool = models.ForeignKey(Tool, related_name='used_tool', on_delete=models.CASCADE)
    test_orientation = models.ForeignKey(TestOrientation, related_name='test_type', on_delete=models.CASCADE)


class TestResults(models.Model):
    test_results_id = models.AutoField(primary_key=True)
    test_results_timestamp = models.DateTimeField(default=now)
    test_results_seed = models.IntegerField()
    test_results_description = models.CharField(max_length=100)
    test_results_test= models.ForeignKey(Test, related_name='test', on_delete=models.CASCADE)
    test_results_profiler= models.ForeignKey(Profiler, related_name='profiledOn', on_delete=models.CASCADE)
    test_results_device= models.ForeignKey(Device, related_name='testedOn', on_delete=models.CASCADE)
    test_results_device_begin_state= models.ForeignKey(DeviceState, related_name='begin_state', on_delete=models.CASCADE,null=True)
    test_results_device_end_state= models.ForeignKey(DeviceState, related_name='end_state', on_delete=models.CASCADE,null=True)

class MethodInvoked(models.Model):
    class Meta:
        unique_together = (('method','test_results'),)
    method = models.ForeignKey(Method, related_name='method', on_delete=models.CASCADE)
    test_results = models.ForeignKey(TestResults, related_name='results', on_delete=models.CASCADE)




   