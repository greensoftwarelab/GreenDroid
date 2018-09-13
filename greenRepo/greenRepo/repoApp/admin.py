from django.contrib import admin
from .models.appRelated import *
from .models.testRelated import *
from .models.metricsRelated import *
# Register your models here.

#app related
admin.site.register(AndroidProject)
admin.site.register(Application)
admin.site.register(AppPermission)
admin.site.register(Class)
admin.site.register(ImportClass)
admin.site.register(Method)
### test related
admin.site.register(TestOrientation)
admin.site.register(Tool)
admin.site.register(Profiler)
admin.site.register(Device)
admin.site.register(DeviceState)
admin.site.register(Test)
admin.site.register(TestResults)
admin.site.register(MethodInvoked)
#metrics related
admin.site.register(Study)
admin.site.register(Metric)
admin.site.register(TestMetric)
admin.site.register(AppMetric)
admin.site.register(ClassMetric)
admin.site.register(MethodMetric)


