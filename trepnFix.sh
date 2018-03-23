#!/bin/bash

source settings.sh

TAG="[GD CLEANER]"
deviceDir="/sdcard/trepn"

#deviceDir=$(cat deviceDir.txt) # TODO improve later (receive as parameter from other script??)

w_echo "Stopping Trepn Profiler"
#shutdown trepn
adb shell am broadcast –a com.quicinc.trepn.stop_profiling
#remove trash files
w_echo "Removing Trash Files from last run"
(adb shell rm -rf $deviceDir/*.db )> /dev/null  2>&1
(adb shell rm -rf $deviceDir/trepn_state )  > /dev/null  2>&1
(adb shell rm -rf $deviceDir/Traces/* ) > /dev/null  2>&1
(adb shell rm -rf $deviceDir/Measures/*  ) > /dev/null  2>&1
(adb shell rm -rf $deviceDir/TracedTests/* ) > /dev/null  2>&1
adb shell "echo 0 > $deviceDir/GDflag"
sleep 3



