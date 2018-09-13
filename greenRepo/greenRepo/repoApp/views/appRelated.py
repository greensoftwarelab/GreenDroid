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



class ProjectView(APIView):
    def get(self, request,projid):
        query=parse_qs(request.META['QUERY_STRING'])
        results = AndroidProject.objects.get(project_id=projid)
        serialize = AndroidProjectWithAppsSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)

    def put(self, request,projid):
        return Response('Not implemented ', HTTP_404_NOT_FOUND)


class ProjectListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = AndroidProject.objects.all()
        if 'project_id' in query:
            results=results.filter(project_id=query['project_id'][0])
        if 'project_build_tool' in query:
            results=results.filter(project_build_tool=query['project_build_tool'][0])
        serialize = AndroidProjectSerializer(results, many=True)
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
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


class AppsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Application.objects.all()
        if 'app_id' in query:
            results=results.filter(app_id=query['app_id'][0])
        if 'app_description' in query:
            results=results.filter(app_description__contains=query['app_description'][0])
        if 'app_version' in query:
            results=results.filter(app_version=query['app_version'][0])
        if 'app_flavor' in query:
            results=results.filter(app_flavor=query['app_flavor'][0])
        if 'app_project' in query:
            try:
                proj = AndroidProject.objects.get(project_id=query['app_project'][0])
                results=results.filter(app_project=proj)
            except Exception as e:
                return Response(status=HTTP_404_NOT_FOUND)
        serialize = ApplicationSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    #adiciona apps mesmo tendo no meio apps que ja existam
    def post(self, request):
        data = JSONParser().parse(request) 
        if isinstance(data,list):
            for item in data:
                try:
                    instance = ApplicationSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = ApplicationSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)
            

class AppsDetailView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        try:
            results = Application.objects.get(app_id=appid)
        except ObjectDoesNotExist:
             return Response(status=HTTP_404_NOT_FOUND)  
        serialize = ApplicationSerializer(results, many=False)
        return Response(serialize.data, HTTP_200_OK)

class AppsTestsView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Test.objects.filter(test_application=appid)
        if 'test_tool' in query:
            results=results.filter(test_tool=query['test_tool'][0].lower())
        if 'test_orientation' in query:
            results=results.filter(test_orientation=query['test_orientation'][0].lower())
        serialize = TestSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


class AppsTestResultsView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        tests = Test.objects.filter(test_application=appid)
        if 'test_tool' in query:
            tests=tests.filter(test_tool=query['test_tool'][0].lower())
        if 'test_orientation' in query:
            tests=tests.filter(test_orientation=query['test_orientation'][0].lower())
        results = TestResults.objects.filter(test_results_test__in=tests.values('id'))
        if 'test_device' in query:
            results=results.filter(test_results_device=query['test_device'][0])
        if 'test_profiler' in query:
            results=results.filter(test_results_profiler=query['test_profiler'][0])
        if 'test_seed' in query:
            results=results.filter(test_results_seed=query['test_seed'][0])
        serialize = TestResultsWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


class AppsClassListView(APIView):
    def get(self, request,appid):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Class.objects.filter(class_app=appid)
        if 'class_is_interface' in query:
            results=results.filter(class_package=query['class_is_interface'][0])
        if 'class_id' in query:
            results=results.filter(class_id=query['class_id'][0])
        if 'class_package' in query:
            results=results.filter(class_package=query['class_package'][0])
        if 'class_name' in query:
            results=results.filter(class_name=query['class_name'][0])
        if 'class_non_acc_mod' in query:
            results=results.filter(class_non_acc_mod__contains=query['class_non_acc_mod'][0])
        if 'class_implemented_ifaces' in query:
            results=results.filter(class_non_acc_mod__contains=query['class_implemented_ifaces'][0])
        if 'class_superclass' in query:
            results=results.filter(class_superclass=query['class_superclass'][0])
        serialize = ClassSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request,appid):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = ClassSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = ClassSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


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
        results = Method.objects.all()
        classes = Class.objects.filter(class_app=appid)
        if 'class_name' in query:
            classes=classes.filter(class_name=query['class_name'][0])
        if 'method_name' in query:
            results=results.filter(method_name=query['method_name'][0])
        results = results.filter(method_class__in=classes.values('class_id'))
        serialize = MethodSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

class AppsMethodsDetailView(APIView):
    def get(self, request,appid,methodid):
        query=parse_qs(request.META['QUERY_STRING'])
        classes = Class.objects.filter(class_app=appid)
        results = Method.objects.filter(method_class__in=classes.values('class_id'))
        results = results.filter(method_name=methodid)
        if 'method_id' in query:
            results=results.filter(method_id=query['method_id'][0])
        serialize = MethodWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)



class MethodsListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Method.objects.all()
        metrics=MethodMetric.objects.all()
        if 'method_class' in query:
            results=results.filter(method_class=query['method_class'][0])
        if 'method_name' in query:
            results=results.filter(method_name=query['method_name'][0])
        if 'method_metric' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_metric=query['method_metric'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass
        if 'method_metric_value' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value=query['method_metric_value'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass 
        if 'method_metric_value_gte' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value__gte=query['method_metric_value_gte'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass  
        serialize = MethodSerializer(results, many=True)
        if 'method_metric_value_lte' in query:
            try:
                metrics=metrics.filter(mm_method__in=results.values('method_id'),mm_value__lte=query['method_metric_value_lte'][0])
                results=results.filter(method_id__in=metrics.values('mm_method'))
            except ObjectDoesNotExist:
                pass  
        serialize = MethodWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)
    
    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = MethodSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=False):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = MethodSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=False):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


class MethodMetricsView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Method.objects.all()
        if 'method_app' in query:
            classes = Class.objects.filter(class_app=query['method_app'][0])
            results=results.filter(method_class__in=classes.values('class_id'))
        if 'method_class' in query:
            classes=classes.filter(method_class=query['method_class'][0])
        if 'method_name' in query:
            results=results.filter(method_name=query['method_name'][0])
        serialize = MethodWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = MethodWithMetricsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=False):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = MethodWithMetricsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=False):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


class AppsMetricsView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Application.objects.all()
        metrics=AppMetric.objects.all()
        if 'app_name' in query:
            results=results.filter(app_name=query['class_name'][0])
        if 'app_metric' in query:
            try:
                metrics=metrics.filter(am_class__in=results.values('app_id'),cm_metric=query['app_metric'][0])
                results=results.filter(app_id__in=metrics.values('am_app'))
            except ObjectDoesNotExist:
                pass
        if 'app_metric_value' in query:
            try:
                metrics=metrics.filter(am_class__in=results.values('app_id'),cm_value=query['app_metric_value'][0])
                results=results.filter(app_id__in=metrics.values('am_app'))
            except ObjectDoesNotExist:
                pass 
        if 'app_metric_value_gte' in query:
            try:
                metrics=metrics.filter(am_class__in=results.values('app_id'),cm_value__gte=query['app_metric_value_gte'][0])
                results=results.filter(app_id__in=metrics.values('am_app'))
            except ObjectDoesNotExist:
                pass
        serialize = MethodSerializer(results, many=True)
        if 'class_metric_value_lte' in query:
            try:
                metrics=metrics.filter(am_class__in=results.values('app_id'),cm_value__lte=query['app__metric_value_lte'][0])
                results=results.filter(app_id__in=metrics.values('am_app'))
            except ObjectDoesNotExist:
                pass  
        serialize = AppWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = AppWithMetricsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=False):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = AppWithMetricsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=False):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


class ClassMetricsView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Class.objects.all()
        metrics=ClassMetric.objects.all()
        if 'class_name' in query:
            results=results.filter(class_name=query['class_name'][0])
        if 'class_metric' in query:
            try:
                metrics=metrics.filter(cm_class__in=results.values('class_id'),cm_metric=query['class_metric'][0])
                results=results.filter(class_id__in=metrics.values('cm_class'))
            except ObjectDoesNotExist:
                pass
        if 'class_metric_value' in query:
            try:
                metrics=metrics.filter(cm_class__in=results.values('class_id'),cm_value=query['class_metric_value'][0])
                results=results.filter(class_id__in=metrics.values('cm_class'))
            except ObjectDoesNotExist:
                pass 
        if 'class_metric_value_gte' in query:
            try:
                metrics=metrics.filter(cm_class__in=results.values('class_id'),cm_value__gte=query['class_metric_value_gte'][0])
                results=results.filter(class_id__in=metrics.values('cm_class'))
            except ObjectDoesNotExist:
                pass
        serialize = MethodSerializer(results, many=True)
        if 'class_metric_value_lte' in query:
            try:
                metrics=metrics.filter(cm_class__in=results.values('class_id'),cm_value__lte=query['class_metric_value_lte'][0])
                results=results.filter(class_id__in=metrics.values('cm_class'))
            except ObjectDoesNotExist:
                pass  
        serialize = ClassWithMetricsSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = ClassithMetricsSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=False):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = ClassWithMetricsSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=False):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)


class MethodInvokedListView(APIView):
    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = MethodInvokedSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except ObjectDoesNotExist as e:
                    print(e)
                    continue
                except Exception as e:
                    print(e)
                    continue
            return Response(data, HTTP_200_OK)
        else:
            try:
                instance = MethodInvokedSerializer(data=data, many=False, partial=True)
                if instance.is_valid(raise_exception=True):
                    instance.save()
                    return Response(instance.data, HTTP_200_OK)
            except Exception as e:
                print(e)
                return Response(instance.data, HTTP_400_BAD_REQUEST)


class ClassesListView(APIView):
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Class.objects.all()
        if 'class_is_interface' in query:
            results=results.filter(class_package=query['class_is_interface'][0])
        if 'class_id' in query:
            results=results.filter(class_id=query['class_id'][0])
        if 'class_package' in query:
            results=results.filter(class_package=query['class_package'][0])
        if 'class_name' in query:
            results=results.filter(class_name=query['class_name'][0])
        if 'class_non_acc_mod' in query:
            results=results.filter(class_non_acc_mod__contains=query['class_non_acc_mod'][0])
        if 'class_implemented_ifaces' in query:
            results=results.filter(class_non_acc_mod__contains=query['class_implemented_ifaces'][0])
        if 'class_superclass' in query:
            results=results.filter(class_superclass=query['class_superclass'][0])
        serialize = ClassSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

    def post(self, request):
        data = JSONParser().parse(request)
        if isinstance(data,list):
            for item in data:
                try:
                    instance = ClassSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = ClassSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                return Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)



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

    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = AppHasPermission.objects.all()
        if 'app_id' in query:
            results=results.filter(application=query['app_id'][0])
        if 'permission' in query:
            results=results.filter(permission=query['permission'][0].lower())
        serialize = AppHasPermissionSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)

