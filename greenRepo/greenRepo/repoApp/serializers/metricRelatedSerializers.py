from rest_framework import serializers

from repoApp.models.metricsRelated import *
from django.utils import timezone

class MetricSerializer(serializers.ModelSerializer):
    class Meta:
        model = Metric
        fields = ('metric_name', 'metric_type','metric_category', 'metric_related_study')


class TestMetricListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        tm = [TestMetric(**item) for item in validated_data]
        return TestMetric.objects.bulk_create(tm)


class TestMetricSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestMetric
        list_serializer_class = TestMetricListSerializer
        fields = ('test_results', 'metric', 'value', 'coeficient')

class StudySerializer(serializers.ModelSerializer):
    class Meta:
        model = Study
        fields = ('study_authors', 'study_publisher','study_year', 'study_isbn','study_organization')




class AppMetricSerializer(serializers.ModelSerializer):
    class Meta:
        model = AppMetric
        fields = ('am_app', 'am_metric','am_value', 'am_unit')









class MethodMetricListSerializer(serializers.ListSerializer):
    def create(self, validated_data):
        methods = [MethodMetric(**item) for item in validated_data]
        return MethodMetric.objects.bulk_create(methods)


class MethodMetricSerializer(serializers.ModelSerializer):
    mm_method = serializers.PrimaryKeyRelatedField(queryset=Method.objects.all())
    class Meta:
        model = MethodMetric
        list_serializer_class = MethodMetricListSerializer
        fields = ('mm_method', 'mm_metric','mm_value', 'mm_coeficient','mm_method_invoked')
        validators = []
        