#!/bin/bash
source settings.sh

#args
monkey_seed=$1
monkey_nr_events=$2
trace=$3
package=$4
localDir=$5
deviceDir=$6
cpu=''
mem=''
nr_processes=''
sdk_level=''
api_level=''
TIMEOUT=300 # 5 minutes

machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
	SED_COMMAND="gsed" #mac
	MKDIR_COMMAND="gmkdir"
	MV_COMMAND="gmv"
	TIMEOUT_COMMAND="gtimeout"

else 
	SED_COMMAND="sed" #linux
	MKDIR_COMMAND="mkdir"
	MV_COMMAND="mv"
	TIMEOUT_COMMAND="timeout"
fi

grantPermissions(){
	adb shell pm grant $1 android.permission.READ_EXTERNAL_STORAGE
	adb shell pm grant $1 android.permission.WRITE_EXTERNAL_STORAGE
	
	#adb shell pm grant $1 android.permission.INTERNET
	#adb shell pm grant $1 android.permission.ACCESS_FINE_LOCATION
	#adb shell pm grant $1 android.permission.ACCESS_WIFI_STATE
	#adb shell pm grant $1 android.permission.READ_PHONE_STATE
	#adb shell pm grant $1 android.permission.ACCESS_NETWORK_STATE
	#adb shell pm grant $1 android.permission.RECEIVE_BOOT_COMPLETED

}


grantPermissions $package
adb shell "echo 1 > $deviceDir/GDflag"
e_echo "actual seed -> $monkey_seed"
getAndroidState cpu mem nr_processes sdk_level api_level
e_echo "begin state: CPU: $cpu % , $MEM: $mem  , Nºprocesses running: $nr_processes sdk level: $sdk_level API:$api_level"
echo "{\"device_state_mem\": \"$mem\", \"device_state_cpu_free\": \"$cpu\",\"device_state_nr_processes_running\": \"$nr_processes\",\"device_state_api_level\": \"$api_level\",\"device_state_android_version\": \"$sdk_level\" }" > $localDir/begin_state.json
adb shell am broadcast -a com.quicinc.trepn.start_profiling -e com.quicinc.trepn.database_file "myfile"
sleep 5
w_echo "clicking home button.."
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1

w_echo "[Measuring] Running monkey tests..."
if [[ $trace == "-TestOriented" ]]; then
	adb shell am broadcast -a com.quicinc.Trepn.UpdateAppState -e com.quicinc.Trepn.UpdateAppState.Value "1" -e com.quicinc.Trepn.UpdateAppState.Value.Desc "started"
fi 
# adb shell -s <seed> -p <package-name> -v <number-of-events> ----pct-syskeys 0 --ignore-crashes 
($TIMEOUT_COMMAND -s 9 $TIMEOUT adb shell monkey  -s $monkey_seed -p $package -v --pct-syskeys 0  $monkey_nr_events) &> logs/monkey.log
RET=$(echo $?)
exceptions=$(grep "Exception" logs/monkey.log)
if [[ "$exceptions" != "" ]]; then
	e_echo "Exception occured during test execution. ignoring app "
	exit -1
fi
if [[ "$RET" != "0" ]]; then
	e_echo "error while running -> error code : $RET"
	echo "$localDir,$monkey_seed" >> logs/timeoutSeed.log
	w_echo "A TIMEOUT Ocurred. killing process of monkey test"
	adb shell ps | grep "com.android.commands.monkey" | awk '{print $2}' | xargs -I{} adb shell kill -9 {}
fi
w_echo "stopped tests. "
if [[ $trace == "-TestOriented" ]]; then
	adb shell am broadcast -a com.quicinc.Trepn.UpdateAppState -e com.quicinc.Trepn.UpdateAppState.Value "0" -e com.quicinc.Trepn.UpdateAppState.Value.Desc "stopped"
fi
sleep 3
echo "stopping..."
adb shell am broadcast -a com.quicinc.trepn.stop_profiling
sleep 6
adb shell am broadcast -a  com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "myfile" -e com.quicinc.trepn.export_csv_output_file "GreendroidResultTrace0"
sleep 1
getAndroidState cpu mem nr_processes sdk_level api_level

adb shell ps | grep "com.android.commands.monkey" | awk '{print $2}' | xargs -I{} adb shell kill -9 {}ß
w_echo "cleaning app cache"
adb shell pm clear $package
w_echo "stopping running app"
adb shell am force-stop $package 
e_echo "end state: CPU: $cpu % , $MEM: $mem  , Nºprocesses running: $nr_processes sdk level: $sdk_level API:$api_level"
echo "{\"device_state_mem\": \"$mem\", \"device_state_cpu_free\": \"$cpu\",\"device_state_nr_processes_running\": \"$nr_processes\",\"device_state_api_level\": \"$api_level\",\"device_state_android_version\": \"$sdk_level\" }" > $localDir/end_state.json
#adb shell am broadcast -a com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "tests" -e com.quicinc.trepn.export_csv_output_file “zzz ”
#echo "exporting.."
adb shell "echo -1 > $deviceDir/GDflag"

grantPermissions $package

#### AFTER EXPORTING, RUN AGAIN  ( TRACING METHODS)
w_echo "[Tracing] Running monkey tests..."
w_echo "monkey command -> $TIMEOUT_COMMAND -s 9 $TIMEOUT adb shell monkey  -s $monkey_seed -p $package -v --pct-syskeys 0  $monkey_nr_events"
($TIMEOUT_COMMAND -s 9 $TIMEOUT adb shell monkey  -s $monkey_seed -p $package -v --pct-syskeys 0  $monkey_nr_events) > logs/monkey.log
RET=$(echo $?)
if [[ "$RET" != "0" ]]; then
	e_echo "error while running -> error code : $RET"
	echo "$localDir,$monkey_seed" >> logs/timeoutSeed.log
	w_echo "A TIMEOUT Ocurred. killing process of monkey test"
	adb shell ps | grep "com.android.commands.monkey" | awk '{print $2}' | xargs -I{} adb shell kill -9 {}
fi





