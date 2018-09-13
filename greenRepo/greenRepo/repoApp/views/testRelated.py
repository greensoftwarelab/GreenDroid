from django.core.exceptions import ObjectDoesNotExist
from rest_framework.parsers import JSONParser
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.status import *
from urllib.parse import parse_qs
from repoApp.models.testRelated import *
# from time import gmtime, strftime
from django.db import IntegrityError
import datetime
from repoApp.serializers.testRelatedSerializers import *
from repoApp.serializers.appRelatedSerializers import MethodSerializer
from repoApp.serializers.metricRelatedSerializers import MethodMetricSerializer, TestMetricSerializer

class TestsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Test.objects.all()
        if 'test_application' in query:
            print((query['test_app'])[0])
            results=results.filter(test_application=query['test_application'][0])
        if 'test_tool' in query:
            results=results.filter(test_tool=query['test_tool'][0])
        if 'test_orientation' in query:
            try:
                orient=TestOrientation.objects.get(test_orientation_id=query['test_orientation'][0])
                results=results.filter(test_orientation=orient.test_orientation_id)
            except ObjectDoesNotExist:
                pass  
        #results = Test.objects.filter(reduce(and_, q)) 
        serialize = TestSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = AndroidProjectSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = AndroidProjectSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                Response(instance.data, HTTP_200_OK)
        return Response(instance.data, HTTP_400_BAD_REQUEST)


class ResultsTestListView(APIView):
    def get(self, request,testid):
        query=parse_qs(request.META['QUERY_STRING'])
        results = TestResults.objects.all()
        if 'test_results_seed' in query:
            results=results.filter(test_results_seed=query['test_results_seed'][0])
        if 'test_results_id' in query:
            results=results.filter(test_results_id=query['test_results_id'][0])
        if 'test_results_profiler' in query:
            results=results.filter(test_results_profiler=query['test_results_profiler'][0])
        if 'test_results_device' in query:
            results=results.filter(test_results_device=query['test_results_device'][0])
        if 'test_results_description' in query:
            results=results.filter(test_results_description__contains=query['test_results_description'][0])
        serializer = TestResultsWithMetricsSerializer(results, many=True)
        return Response(serializer.data, status=HTTP_200_OK)


    def post(self, request,testid):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = TestResultsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = TestResultsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


def getMetrics(initial_data):
    metrics = []
    try:
        for x in initial_data:
            print(x)
            metric = x['method_metrics']
            serializer= MethodMetricSerializer (data=metric,many=isinstance(metric,list), partial=True)
            if serializer.is_valid(raise_exception=True):
                instance = serializer.create(serializer.validated_data)
                instance.save()
    except Exception as e:
        raise e
        return

class ResultsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = TestResults.objects.all()
        if 'test_results_seed' in query:
            results=results.filter(test_results_seed=query['test_results_seed'][0])
        if 'test_results_id' in query:
            results=results.filter(test_results_id=query['test_results_id'][0])
        if 'test_results_profiler' in query:
            results=results.filter(test_results_profiler=query['test_results_profiler'][0])
        if 'test_results_device' in query:
            results=results.filter(test_results_device=query['test_results_device'][0])
        if 'test_results_description' in query:
            results=results.filter(test_results_description__contains=query['test_results_description'][0])
        serializer = TestResultsSerializer(results, many=True)


    def post(self, request):
        data = JSONParser().parse(request)
        serializer = MethodMetricSerializer(data=data,many=isinstance(data, list), partial=True)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = TestResultsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = TestResultsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)

class TestMetricsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = TestMetric.objects.all()
        if 'test_metric' in query:
            results=results.filter(metric=query['test_metric'][0])
        if 'test_value' in query:
            results=results.filter(value=query['test_value'][0])
        if 'test_value_text' in query:
            results=results.filter(value_text=query['test_value_text'][0])
        serialize = TestMetricSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = TestResultsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = TestResultsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)



class TestResultsListView(APIView):
    def post(self, request):
        data = JSONParser().parse(request)
        serializer = TestResultsSerializer(data=data, many=isinstance(data,list), partial=True)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            #erialize = TestResultsSerializer(instance, many=isinstance(data,list))
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)





