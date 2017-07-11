#!/bin/bash
source settings.sh

pack=$1
testPack=$2
deviceDir=$3
localDir=$4
projType=$5

TAG="[APP RUNNER]"
echo ""

#runner="com.zutubi.android.junitreport.JUnitReportTestRunner"
runner="android.test.InstrumentationTestRunner"

i_echo "$TAG Running"

if [ "$projType" == "-gradle" ]; then
	FOLDER=$6
	chmod +x $FOLDER/gradlew
	echo  "$TAG Building and running tests....."
	cd $FOLDER ; ($FOLDER/gradlew connectedAndroidTest &> buildStatus.log)
else

	echo "$TAG Cleaning previous files"  ##RR
	adb shell rm -rf "$deviceDir/allMethods.txt"  ##RR
	adb shell rm -rf "$deviceDir/Traces/*"  ##RR
	adb shell rm -rf "$deviceDir/TracedMethods.txt"  ##RR
	adb shell rm -rf "$deviceDir/Measures/*"  ##RR

	rm -rf $localDir/*.csv
	
	echo "$TAG Running the tests (Measuring)"
	adb shell "echo 1 > $deviceDir/GDflag"
	#adb shell am instrument -e reportFile ALL-TEST.xml -e reportDir $deviceDir/$pack -e filterTraces false -w $testPack/com.zutubi.android.junitreport.JUnitReportTestRunner
	adb shell am instrument -w $testPack/$runner

	#Stop the app, if it is still running
	adb shell am force-stop $pack
	adb shell am force-stop $testPack
	adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1
	
	echo "$TAG Running the tests (Tracing)"
	adb shell "echo -1 > $deviceDir/GDflag"
	#adb shell am instrument -w $testPack/com.zutubi.android.junitreport.JUnitReportTestRunner
	adb shell am instrument -w $testPack/$runner

	adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1

fi

i_echo "$TAG Pulling result files"
#adb pull $deviceDir $localDir

adb shell ls "$deviceDir/Measures/" | sed -r 's/[\r]+//g' | egrep "*.csv" |  xargs -I{} adb pull $deviceDir/Measures/{} $localDir
#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
adb shell ls "$deviceDir/Traces/" | sed -r 's/[\r]+//g' | egrep "*.txt" | xargs -I{} adb pull $deviceDir/Traces/{} $localDir
exit
