#!/bin/bash
source settings.sh

#args
monkey_seed=$1
monkey_nr_events=$2
trace=$3
package=$4
cpu=''
mem=''
nr_processes=''


machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
	SED_COMMAND="gsed" #mac
	MKDIR_COMMAND="gmkdir"
	MV_COMMAND="gmv"
else 
	SED_COMMAND="sed" #linux
	MKDIR_COMMAND="mkdir"
	MV_COMMAND="mv"	
fi

e_echo "actual seed -> $monkey_seed"
getAndroidState cpu mem nr_processes
e_echo "begin state: CPU: $cpu % , $MEM: $mem  , Nºprocesses running: $nr_processes"
adb shell am broadcast -a com.quicinc.trepn.start_profiling -e com.quicinc.trepn.database_file "myfile"
sleep 5
echo "updating.."
w_echo "running tests ......"
if [[ $trace == "-TestOriented" ]]; then
	adb shell am broadcast -a com.quicinc.Trepn.UpdateAppState -e com.quicinc.Trepn.UpdateAppState.Value "1" -e com.quicinc.Trepn.UpdateAppState.Value.Desc "started"
fi 
(adb shell monkey  -s $monkey_seed -p $package -v $monkey_nr_events --pct-syskeys 0) > /dev/null
if [[ $trace == "-TestOriented" ]]; then
	adb shell am broadcast -a com.quicinc.Trepn.UpdateAppState -e com.quicinc.Trepn.UpdateAppState.Value "0" -e com.quicinc.Trepn.UpdateAppState.Value.Desc "stopped"
fi
sleep 3
echo "stopping.."
adb shell am broadcast -a com.quicinc.trepn.stop_profiling
sleep 6
getAndroidState cpu mem nr_processes
sleep 1
e_echo "end state: CPU: $cpu % , $MEM: $mem  , Nºprocesses running: $nr_processes"
#adb shell am broadcast -a com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "tests" -e com.quicinc.trepn.export_csv_output_file “zzz ”
adb shell am broadcast -a  com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "myfile" -e com.quicinc.trepn.export_csv_output_file "GreendroidResultTrace0"
#echo "exporting.."
sleep 4