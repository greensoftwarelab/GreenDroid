#!/bin/bash

deviceDir="sdcard/trepn"
AppDir=$(cat lastTranformedApp.txt)
chmod +x $AppDir/gradlew
cd $AppDir ; %AppDir/gradlew cat
localDir= "$HOME/GDresults/retrys"
i_echo "$TAG Pulling result files"
#check if trepn worked correctly
Nmeasures=$(adb shell ls "$deviceDir/Measures/" | wc -l)
Ntraces=$(adb shell ls "$deviceDir/Traces/" | wc -l)
echo "Nº measures: $Nmeasures Nºtraces $Ntraces"
if [ $Nmeasures -le "0" ] || [ $Ntraces -le "0" ] || [ $Nmeasures -ne $Ntraces ] ; then 
	e_echo "[GD ERROR] Something went wrong. Try restart trepn (and delete .db and state files in trepn folder) or check GDflag"
fi
echo $localDir
adb shell ls "$deviceDir/Measures/" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.csv" |  xargs -I{} adb pull $deviceDir/Measures/{} $localDir
#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
adb shell ls "$deviceDir/Traces/" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.txt" | xargs -I{} adb pull $deviceDir/Traces/{} $localDir
 
