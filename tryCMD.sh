#!/bin/bash
source settings.sh



ID="2b335fc0-2cb0-4f51-a91e-129ef220a1f92"
suc=$(cat logs/success.log | sort -u | uniq | grep $ID )
if [[ -z $suc  ]]; then
	echo "ha cu na batata"
else
	echo "nada"
fi


##
#sleep 3
#echo "starting.."
#adb shell am startservice --user 0 com.quicinc.trepn/.TrepnService
#sleep 10
#adb shell am broadcast -a com.quicinc.trepn.start_profiling -e com.quicinc.trepn.database_file "myfile"
#sleep 10
#echo "updating.."
#adb shell am broadcast -a com.quicinc.Trepn.UpdateAppState -e com.quicinc.Trepn.UpdateAppState.Value "1" -e com.quicinc.Trepn.UpdateAppState.Value.Desc "cachaco"
#sleep 5
#echo "profiling.."
#adb shell am broadcast -a com.quicinc.trepn.stop_profiling
#sleep 10
#adb shell am broadcast -a com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "tests" -e com.quicinc.trepn.export_csv_output_file “zzz ”
#adb shell am broadcast -a  com.quicinc.trepn.export_to_csv -e com.quicinc.trepn.export_db_input_file "myfile" -e com.quicinc.trepn.export_csv_output_file "mycsv"
#echo "exporting.."
#sleep 10
#adb shell am stopservice com.quicinc.trepn/.TrepnService
