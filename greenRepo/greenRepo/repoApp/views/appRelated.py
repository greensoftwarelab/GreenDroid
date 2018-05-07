from django.core.exceptions import ObjectDoesNotExist
from rest_framework.parsers import JSONParser
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.status import *
from urllib.parse import parse_qs
from repoApp.models.testRelated import *
from repoApp.models.appRelated import *
from repoApp.models.metricsRelated import *
# from time import gmtime, strftime
from django.db import IntegrityError
from django.core.exceptions import ValidationError
import datetime
from repoApp.serializers.testRelatedSerializers import *
from repoApp.serializers.appRelatedSerializers import *
from repoApp.serializers.metricRelatedSerializers import *


class AppsListView(APIView):
    def get(self, request):
        print('paxim')
        query=parse_qs(request.META['QUERY_STRING'])
        results = Application.objects.all()
        if 'app_id' in query:
            print((query['app_id'])[0])
            results=results.filter(test_application=query['app_id'][0])
        if 'app_language' in query:
            results=results.filter(app_language=query['app_language'][0])
        if 'app_build_tool' in query:
            try:
                orient=AppBuildTool.objects.get(build_id=query['app_build_tool'][0])
                results=results.filter(app_build_tool=orient.build_id)
            except ObjectDoesNotExist:
                pass  
        serialize = ApplicationSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


    def post(self, request):
        data = JSONParser().parse(request)
        try: 
            serializer = ApplicationSerializer(data=data, partial=True)
            if serializer.is_valid(raise_exception=True):
    
                instance = serializer.create(serializer.validated_data)
                instance.save()
                serialize = ApplicationSerializer(instance, many=False)
                return Response(serialize.data, HTTP_200_OK)
        except Exception as ex:
            print(ex)
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)
        

class AppsDetailView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = Application.objects.get(app_id=appid)
        except ObjectDoesNotExist:
             return Response("Application not present in database", HTTP_400_BAD_REQUEST)  
        serialize = ApplicationSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)



class ResultsTestListView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            res = AppMetric.objects.filter(am_app=appid)
        except ObjectDoesNotExist as e:
            return Response('The specified time restriction doesnt exist for that space', status=HTTP_400_BAD_REQUEST)
        serializer = TestResultsFullSerializer(res, many=True)
        return Response(serializer.data, status=HTTP_200_OK)


    def post(self, request):
        return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)


class AppsMethodsListView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = AppHasMethod.objects.filter(application=appid)
            #results = MethodMetric.objects.filter(mm_method__in=res.values('method'))
        except ObjectDoesNotExist:
             return Response("Application not present in database", HTTP_400_BAD_REQUEST)  
        #serialize = MethodMetricSerializer(results, many=True)
        serialize = AppHasMethodFullSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

class AppsMethodsDetailView(APIView):
    def get(self, request,appid,methodid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = AppHasMethod.objects.get(application=appid,method=methodid)
        except ObjectDoesNotExist:
             return Response("Method of that App not present in database", HTTP_400_BAD_REQUEST)  
        serialize = AppHasMethodFullSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)

class MethodsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Method.objects.all()
        metrics=MethodMetric.objects.all()
        if 'class' in query:
            results=results.filter(method_class=query['class'][0])
        if 'method' in query:
            results=results.filter(method_name=query['method'][0])
        if 'metric' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_metric=query['metric'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass
        if 'metric_value' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value=query['metric_value'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass 
        if 'metric_value_gte' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value__gte=query['metric_value_gte'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass  
        serialize = MethodSerializer(results, many=True)
        if 'metric_value_lte' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value__lte=query['metric_value_lte'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass  
        serialize = MethodWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)
    
    def post(self, request):
        data = JSONParser().parse(request)
        serializer = MethodSerializer(data=data, many=isinstance(data,list), partial=True)
        try:
            if serializer.is_valid(raise_exception=True):
                print('bb')
                if isinstance(data,list):
                    for item in data:
                        try:
                            instance = MethodSerializer(data=item, many=False, partial=True)
                            if instance.is_valid(raise_exception=True):
                                instance.save()
                            #serializer = TestSerializer(instance, many=isinstance(data,list))
                        except Exception as e:
                            print(e)
                            continue
                return Response(serializer.data, HTTP_200_OK)
            else:
                return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)
        except Exception as e:
            print(e)
            return Response(serializer.data, HTTP_200_OK)


class MethodsDetailView(APIView):
    def get(self, request,appid,methodid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = AppHasMethod.objects.get(application=appid,method=methodid)
        except ObjectDoesNotExist:
             return Response("Method of that App not present in database", HTTP_400_BAD_REQUEST)  
        serialize = AppHasMethodFullSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)

class MethodMetricsView(APIView):
    def get(self, request,appid,methodid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = AppHasMethod.objects.get(application=appid,method=methodid)
        except ObjectDoesNotExist:
             return Response("Method of that App not present in database", HTTP_400_BAD_REQUEST)  
        serialize = AppHasMethodFullSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        #print(data)
        serializer = MethodMetricSerializer(data=data,many=isinstance(data, list), partial=True)
            if serializer.is_valid(raise_exception=True):
            if isinstance(data,list):
                for item in data:
                    try:
                        instance = MethodMetricSerializer(data=item, many=False, partial=True)
                        if instance.is_valid(raise_exception=True):
                            instance.save()
                        #serializer = TestSerializer(instance, many=isinstance(data,list))
                    except IntegrityError as e:
                        continue
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)


class MethodInvokedListView(APIView):
    def post(self, request):
        data = JSONParser().parse(request)
        serializer = MethodInvokedSerializer(data=data, many=isinstance(data,list), partial=True)
        if serializer.is_valid(raise_exception=True):
            if isinstance(data,list):
                for item in data:
                    try:
                        instance = MethodInvokedSerializer(data=item, many=False, partial=True)
                        if instance.is_valid(raise_exception=True):
                            instance.save()
                        #serializer = TestSerializer(instance, many=isinstance(data,list))
                    except IntegrityError as e:
                        continue
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)




class AppHasPermissionListView(APIView):
    def post(self, request):
        data = JSONParser().parse(request)
        serializer = AppHasPermissionSerializer(data=data, many=isinstance(data,list), partial=True)
        if serializer.is_valid(raise_exception=True):
            if isinstance(data,list):
                for item in data:
                    try:
                        instance = AppHasPermissionSerializer(data=item, many=False, partial=True)
                        if instance.is_valid(raise_exception=True):
                            instance.save()
                        #serializer = TestSerializer(instance, many=isinstance(data,list))
                    except IntegrityError as e:
                        continue
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)



