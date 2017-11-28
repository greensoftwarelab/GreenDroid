#!/bin/bash

#trepDir="/sdcard/trepn"
trepDir=$(cat deviceDir.txt) # TODO improve later (receive as parameter from other script??)

#shutdown trepn
adb shell am broadcast –a com.quicinc.trepn.stop_profiling
#remove trash files
adb shell rm -rf $deviceDir/*.db
adb shell rm -rf $deviceDir/trepn_state
adb shell echo "0" > $deviceDir/GDflag #reset

