from rest_framework import serializers

from repoApp.models.appRelated import *
from repoApp.models.metricsRelated import *
from repoApp.serializers.metricRelatedSerializers import MethodMetricSerializer, ClassMetricSerializer, AppMetricSerializer
from django.utils import timezone




class AndroidProjectSerializer(serializers.ModelSerializer):
    def create(self, validated_data):
        obj = AndroidProject.objects.create(**validated_data)
        obj.project_build_tool = obj.project_build_tool.lower()
        obj.save()
        return obj
    class Meta:
        model = AndroidProject
        fields = ('project_id', 'project_build_tool', 'project_desc')
        validators = []


class AndroidProjectWithAppsSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(AndroidProjectWithAppsSerializer, self).__init__(*args, **kwargs) 
        self.fields['project_apps'] = serializers.SerializerMethodField()
     
    def get_project_apps(self, proj):
        apps = Application.objects.filter(app_project=proj.project_id)
        return ApplicationSerializer(instance=apps,  many=True).data  
    class Meta:
        model = AndroidProject
        fields = ('project_id', 'project_build_tool', 'project_desc')
        validators = []



class ApplicationListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        methods = []
        for item in validated_data:
            try:
                m = Application(**item)
                m.save()
     #meti agora
            except Exception as e:
                continue
        return True

class ApplicationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Application
        list_serializer_class =ApplicationListSerializer
        fields = ('app_id', 'app_location', 'app_description','app_version','app_flavor','app_build_type', 'app_project')
        validators = []


#class ProjectTypeSerializer(serializers.ModelSerializer):
#    class Meta:
#        model = ProjectType
#        fields = ('project_type_id', 'project_type_designation')


class AppPermissionSerializer(serializers.ModelSerializer):
    class Meta:
        model = AppPermission
        fields = ('name')

class ImportClassSerializer(serializers.ModelSerializer):
    class Meta:
        model = ImportClass
        fields = ('import_name', 'import_class')



class AppPermissionSerializer(serializers.ModelSerializer):
    class Meta:
        model = AppPermission
        fields = ('name')



class ClassListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        tm = [Class(**item) for item in validated_data]
        return Class.objects.bulk_create(tm)





class ClassSerializer(serializers.ModelSerializer):
    class Meta:
        model = Class
        list_serializer_class =ClassListSerializer
        fields = ('class_id', 'class_name', 'class_package', 'class_non_acc_mod',
            'class_app', 'class_acc_modifier', 'class_superclass', 'class_is_interface' ,'class_implemented_ifaces')
        validators=[]        

#TODO classwithImportsSerializer


class ClassWithMetricsSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(ClassWithMetricsSerializer, self).__init__(*args, **kwargs) 
        self.fields['class_metrics'] = serializers.SerializerMethodField()
     
    def get_class_metrics(self, classe):
        met = ClassMetric.objects.filter(cm_class=classe.class_id)
        return ClassMetricSerializer(instance=met,  many=True).data  

    class Meta:
        model = Class
        list_serializer_class =ClassListSerializer
        fields = ('class_id', 'class_name', 'class_package', 'class_non_acc_mod',
            'class_app', 'class_acc_modifier', 'class_superclass', 'class_is_interface' ,'class_implemented_ifaces')
        validators = []


class AppWithMetricsSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(AppWithMetricsSerializer, self).__init__(*args, **kwargs) 
        self.fields['app_metrics'] = serializers.SerializerMethodField()
     
    def get_app_metrics(self, app):
        met = AppMetric.objects.filter(am_app=app.app_id)
        return AppMetricSerializer(instance=met,  many=True).data  

    class Meta:
        model = Application
        #list_serializer_class =ClassListSerializer
        list_serializer_class =ApplicationListSerializer
        fields = ('app_id', 'app_location', 'app_description','app_version','app_flavor','app_build_type', 'app_project')
        validators = []

class ClassWithImportsSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(ClassWithImportsSerializer, self).__init__(*args, **kwargs) 
        self.fields['class_imports'] = serializers.SerializerMethodField()
     
    def get_class_imports(self, classe):
        met = ImportClass.objects.filter(import_class=classe.import_class)
        return ImportClassSerializer(instance=met,  many=True).data  

    class Meta:
        model = Class
        list_serializer_class =ClassListSerializer
        fields = ('class_id', 'class_name', 'class_package', 'class_non_acc_mod',
            'class_app', 'class_acc_modifier', 'class_superclass', 'class_is_interface' ,'class_implemented_ifaces')
        validators = []

class AppHasPermissionListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        perms=[]
        for item in validated_data:
            try:
                m = AppHasPermission(**item)
                perms.append(m)
            except Exception as e:
                continue
        return AppHasPermission.objects.bulk_create(perms)


class AppHasPermissionSerializer(serializers.ModelSerializer):
    class Meta:
        list_serializer_class = AppHasPermissionListSerializer
        model = AppHasPermission
        fields = ('application', 'permission')
        validators = []

#class TestCaseSerializer(serializers.ModelSerializer):
#    class Meta:
#        model = TestCase
#        fields = ('test_case_id', 'test_case_name', 'test_case_framework', 'test_case_version', 'test_file')




class MethodListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        methods = []
        for item in validated_data:
            try:
                m = Method(**item)
                m.save()
     #meti agora
            except Exception as e:
                continue
        return True
        #return Method.objects.bulk_create(methods)


class MethodWithMetricsSerializer(serializers.ModelSerializer):
    def __init__(self, *args, **kwargs):
        super(MethodWithMetricsSerializer, self).__init__(*args, **kwargs) 
        self.fields['method_metrics'] = serializers.SerializerMethodField()
     
    def get_method_metrics(self, method):
        met = MethodMetric.objects.filter(mm_method=method.method_id)
        return MethodMetricSerializer(instance=met,  many=True).data  

    class Meta:
        model = Method
        list_serializer_class = MethodListSerializer
        fields = ('method_id', 'method_name','method_acc_modifier', 'method_non_acc_mod','method_class')
        validators = []


class MethodSerializer(serializers.ModelSerializer):
    class Meta:
        model = Method
        #list_serializer_class = MethodListSerializer
        fields = ('method_id', 'method_name', 'method_class','method_acc_modifier', 'method_non_acc_mod')
        validators = []

