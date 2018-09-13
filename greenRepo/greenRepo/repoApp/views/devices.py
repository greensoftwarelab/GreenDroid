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

class DevicesListView(APIView):
    #'device_serial_number','device_brand','device_model'
    def get(self, request):
        query=parse_qs(request.META['QUERY_STRING'])
        results = Device.objects.all()
        if 'device_serial_number' in query:
            print((query['serial_number'])[0])
            results=results.filter(device_serial_number=query['device_serial_number'][0])
        if 'device_brand' in query:
            print(query['brand'][0])
            results=results.filter(device_brand=query['device_brand'][0])
        if 'device_model' in query:
            results=results.filter(device_model=query['device_model'][0])
        #results = Test.objects.filter(reduce(and_, q))
        serialize = DeviceSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


    def post(self, request):
        data = JSONParser().parse(request) 
        if isinstance(data,list):
            for item in data:
                try:
                    instance = DeviceSerializer(data=item, many=False, partial=True)
                    if instance.is_valid(raise_exception=True):
                        instance.save()
                except Exception as e:
                    continue
            return Response(data, HTTP_200_OK)
        else:
            instance = DeviceSerializer(data=data, many=False, partial=True)
            if instance.is_valid(raise_exception=True):
                instance.save()
                Response(instance.data, HTTP_200_OK)
            return Response(instance.data, HTTP_400_BAD_REQUEST)
