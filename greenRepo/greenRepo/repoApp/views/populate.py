from django.core.management import call_command
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.status import *

from repoApp.models.appRelated import Method , AppPermission, AppBuildTool
from repoApp.models.metricsRelated import Metric
from repoApp.models.testRelated import TestOrientation , Tool, Profiler,Device, DeviceState

class PopulateView(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
        try:
            a = Metric()
            a.metric_name="WifiState"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="MobileDataState"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="ScreenState"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="BatteryStatus"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="WifiRSSILevel"
            a.metric_category='h'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="BluetoothState"
            a.metric_category='h'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="GpuFrequency"
            a.metric_category='m'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="CpuLoadNormalized"
            a.metric_category='m'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="GpsState"
            a.metric_category='h'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="TotalTime"
            a.metric_type='d'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="TotalEnergy"
            a.metric_type='d'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="TotalCoverage"
            a.metric_category='m'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="Coverage"
            a.metric_type='h'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="CC"
            a.metric_category='m'
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="LoC"
            a.metric_type='s'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="NrArgs"
            a.metric_category='m'
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="androidapis"
            a.metric_category='a'
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="JavaApis"
            a.metric_category='a'
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_category='a'
            a.metric_name="unknownapis"
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_category='m'
            a.metric_name="isStatic"
            a.metric_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="energy"
            a.metric_category='m'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="gpstate"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="MobileDatastate"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="wifistate"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="CPULoad"
            a.metric_type='d'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="ScreenState"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="BatteryCharging"
            a.metric_category='h'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="GPULoad"
            a.metric_category='m'
            a.metric_type='d'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="wifiRSSILevel"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="Bluetoothstate"
            a.metric_type='d'
            a.metric_category='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="Memory"
            a.metric_type='d'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Metric()
            a.metric_name="time"
            a.metric_type='d'
            a.metric_category='m'
            a.save()
        except Exception as e:
            print(e)
        ######## TestOrientation
        try:
            a = TestOrientation()
            a.test_orientation_designation="methodoriented"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = TestOrientation()
            a.test_orientation_designation="testoriented"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = TestOrientation()
            a.test_orientation_designation="applicationtoriented"
            a.save()
        except Exception as e:
            print(e)
        ######## Tools
        try:
            a = Tool()
            a.tool_name="Junit"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Tool()
            a.tool_name="monkey"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Tool()
            a.tool_name="robotium"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Tool()
            a.tool_name="unittests"
            a.save()
        except Exception as e:
            print(e)
        ######### Profiler
        try:
            a = Profiler()
            a.profiler_name="monsoon"
            a.profiler_type='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Profiler()
            a.profiler_name="trepn"
            a.profiler_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Profiler()
            a.profiler_name="odroid"
            a.profiler_type='h'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Profiler()
            a.profiler_name="greendroid"
            a.profiler_type='s'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Profiler()
            a.profiler_name="power Tutor"
            a.profiler_type='m'
            a.save()
        except Exception as e:
            print(e)
        ########### Device
        try:
            a = Device()
            a.device_serial_number="XFF2"
            a.device_brand='Xiaomi'
            a.device_model='Mi 5'
            a.save()
        except Exception as e:
            print(e)
        try:
            a = Device()
            a.device_serial_number="X2222"
            a.device_brand='Nexus'
            a.device_model='4'
            a.save()
        except Exception as e:
            print(e)
        ########### Device
        try:
            a = DeviceState()
            a.device_state_mem=100
            a.device_state_nr_processes_running=10
            a.device_state_cpu_free=90
            a.device_state_api_level=25
            a.device_state_android_version=7.1
            a.save()
        except Exception as e:
            print(e)
        try:
            a = DeviceState()
            a.device_state_mem=960
            a.device_state_nr_processes_running=11
            a.device_state_cpu_free=30
            a.device_state_api_level=22
            a.device_state_android_version=6.0
            a.save()
        except Exception as e:
            print(e)
        ######## Permissions
        try:
            a = AppPermission()
            a.name="ACCESS_LOCATION_EXTRA_COMMANDS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_NETWORK_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_NOTIFICATION_POLICY"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_NETWORK_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_WIFI_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BLUETOOTH"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BLUETOOTH_ADMIN"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BROADCAST_STICKY"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CHANGE_NETWORK_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CHANGE_WIFI_MULTICAST_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CHANGE_WIFI_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="DISABLE_KEYGUARD"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="GET_PACKAGE_SIZE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="INSTALL_SHORTCUT"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="INTERNET"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="KILL_BACKGROUND_PROCESSES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="MANAGE_OWN_CALLS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="MODIFY_AUDIO_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="NFC"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_SYNC_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="INTERNET"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="KILL_BACKGROUND_PROCESSES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="MANAGE_OWN_CALLS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="MODIFY_AUDIO_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="NFC"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_SYNC_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_SYNC_STATS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="RECEIVE_BOOT_COMPLETED"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REORDER_TASKS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_COMPANION_RUN_IN_BACKGROUND"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_COMPANION_USE_DATA_IN_BACKGROUND"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_DELETE_PACKAGES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_INSTALL_PACKAGES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="SET_ALARM"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="SET_WALLPAPER"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="SET_WALLPAPER_HINTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="TRANSMIT_IR"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="USE_FINGERPRINT"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="VIBRATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WAKE_LOCK"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_SYNC_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_ACCESSIBILITY_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_AUTOFILL_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_CARRIER_SERVICES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_CHOOSER_TARGET_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_CONDITION_PROVIDER_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_DEVICE_ADMIN"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_DREAM_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_INCALL_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_INPUT_METHOD"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_MIDI_DEVICE_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_NFC_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_NOTIFICATION_LISTENER_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_PRINT_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_SCREENING_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_TELECOM_CONNECTION_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_TEXT_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_TV_INPUT"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_VISUAL_VOICEMAIL_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_VPN_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BIND_VR_LISTENER_SERVICE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CLEAR_APP_CACHE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="MANAGE_DOCUMENTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_VOICEMAIL"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="REQUEST_INSTALL_PACKAGES"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="SYSTEM_ALERT_WINDOW"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_SETTINGS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_VOICEMAIL"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_CALENDAR"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_CALENDAR"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CAMERA"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_CONTACTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_CONTACTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="GET_ACCOUNTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_FINE_LOCATION"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ACCESS_COARSE_LOCATION"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="RECORD_AUDIO"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_PHONE_STATE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_CONTACTS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_PHONE_NUMBERS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="CALL_PHONE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ANSWER_PHONE_CALLS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_CALL_LOG"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_CALL_LOG"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ADD_VOICEMAIL"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="USE_SIP"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="PROCESS_OUTGOING_CALLS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="ANSWER_PHONE_CALLS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="BODY_SENSORS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="SEND_SMS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="RECEIVE_SMS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_SMS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="RECEIVE_WAP_PUSH"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="RECEIVE_MMS"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="READ_EXTERNAL_STORAGE"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppPermission()
            a.name="WRITE_EXTERNAL_STORAGE"
            a.save()
        except Exception as e:
            print(e)

        ######## APP AppBuildTool
        try:
            a = AppBuildTool()
            a.build_id="Gradle"
            a.save()
        except Exception as e:
            print(e)
        try:
            a = AppBuildTool()
            a.build_id="SDK"
            a.save()
        except Exception as e:
            print(e)
        return Response("nice", HTTP_200_OK)
       


