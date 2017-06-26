#!/bin/bash
TAG="[APP RUNNER]"

pack=$1
testPack=$2
deviceDir=$3
localDir=$4
projType=$5

#runner="com.zutubi.android.junitreport.JUnitReportTestRunner"
runner="android.test.InstrumentationTestRunner"

if [ "$projType" == "-gradle" ]; then
	FOLDER=$6
	chmod +x $FOLDER/gradlew
	echo  "$TAG Building and running tests....."
	cd $FOLDER ; ($FOLDER/gradlew connectedAndroidTest &> buildStatus.log)
else

	echo "$TAG Cleaning previous files"  ##RR
	adb shell rm -rf "$deviceExternal/trepn/allMethods.txt"  ##RR
	adb shell rm -rf "$deviceExternal/trepn/Traces/*txt"  ##RR
	adb shell rm -rf "$deviceExternal/trepn/*.csv"  ##RR
	
	echo "$TAG Running the tests (Tracing)"
	adb shell "echo -1 > $deviceDir/GDflag"
	#adb shell am instrument -e reportFile ALL-TEST.xml -e reportDir $deviceDir/$pack -e filterTraces false -w $testPack/com.zutubi.android.junitreport.JUnitReportTestRunner
	adb shell am instrument -w $testPack/$runner

	#Stop the app, if it is still running
	adb shell am force-stop $pack
	adb shell am force-stop $testPack
	adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1
	
	echo "$TAG Running the tests (Measuring)"
	adb shell "echo 1 > $deviceDir/GDflag"
	#adb shell am instrument -w $testPack/com.zutubi.android.junitreport.JUnitReportTestRunner
	adb shell am instrument -w $testPack/$runner

fi

echo "$TAG Pulling result files"
#adb pull $deviceDir $localDir

adb shell ls "$deviceDir/*.csv" | tr '\r' '' | xargs -n1 adb pull
adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
#adb shell ls "$deviceDir/Traces/*.txt" | tr '\r' ' ' | xargs -n1 adb pull 
exit