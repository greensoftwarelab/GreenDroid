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
        if 'serial_number' in query:
            print((query['serial_number'])[0])
            results=results.filter(device_serial_number=query['serial_number'][0])
        if 'brand' in query:
            print(query['brand'][0])
            results=results.filter(device_brand=query['brand'][0])
        if 'model' in query:
            results=results.filter(device_model=query['model'][0])
        #results = Test.objects.filter(reduce(and_, q))
        serialize = DeviceSerializer(results, many=True)
        return Response(serialize.data, HTTP_200_OK)


    def post(self, request):
        data = JSONParser().parse(request)
        try: 
            serializer = DeviceSerializer(data=data, partial=True)
            if serializer.is_valid(raise_exception=True):
    
                instance = serializer.create(serializer.validated_data)
                instance.save()
                serialize = DeviceSerializer(instance, many=False)
                return Response(serialize.data, HTTP_200_OK)
            else:
                return Response("Invalid data", HTTP_400_BAD_REQUEST)
        except Exception as ex:
            return Response(serializer.data, HTTP_200_OK)
        else:
            return Response('Internal error or malformed JSON ', HTTP_400_BAD_REQUEST)
