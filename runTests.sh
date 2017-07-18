#!/bin/bash
source settings.sh

pack=$1
testPack=$2
deviceDir=$3
localDir=$4
projType=$5

TIMEOUT="1200" #20 minutes (60*20)

TAG="[APP RUNNER]"
echo ""

#runner="android.support.test.runner.AndroidJUnitRunner"
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
	
	adb shell am instrument -w $testPack/$runner &> runStatus.log

	missingInstrumentation=$(grep "Unable to find instrumentation info for" runStatus.log)
	flagInst="0"
	if [[ -n "$missingInstrumentation" ]]; then
		# Something went wrong during instalation and run. 
		# Let's try running all existing instrumentations (should not be bigger than one)
		flagInst="1"
		allInstrumentations=($(adb shell pm list instrumentation | cut -f2 -d: | cut -f1 -d\ ))
		if [[ "${#allInstrumentations[@]}" == "1" ]]; then
			for i in ${allInstrumentations[@]}; do
				timeout -s 9 $TIMEOUT adb shell am instrument -w $i &> runStatus.log
				RET=$(echo $?)
				if [[ "$RET" != 0 ]]; then
					./forceUninstall.sh
					exit -1
				fi
			done
		else
			e_echo "$TAG Wrong number of instrumentations: Found ${#allInstrumentations[@]}, Expected 1."
		fi
	fi

	#Stop the app, if it is still running
	adb shell am force-stop $pack
	adb shell am force-stop $testPack
	adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1
	
	echo "$TAG Running the tests (Tracing)"
	adb shell "echo -1 > $deviceDir/GDflag"
	
	if [[ "$flagInst" == 1 ]]; then
		allInstrumentations=($(adb shell pm list instrumentation | cut -f2 -d: | cut -f1 -d\ ))
		if [[ "${#allInstrumentations[@]}" == "1" ]]; then
			for i in ${allInstrumentations[@]}; do
				timeout -s 9 $TIMEOUT adb shell am instrument -w $i &> runStatus.log
				RET=$(echo $?)
				if [[ "$RET" != 0 ]]; then
					./forceUninstall.sh
					exit -1
				fi
			done
		else
			e_echo "$TAG Wrong number of instrumentations: Found ${#allInstrumentations[@]}, Expected 1."
		fi
	else
		adb shell am instrument -w $testPack/$runner &> runStatus.log
	fi
	adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME > /dev/null 2>&1

fi

# TODO: Include output check from 'adb shell instrument' to assert that the tests were actually executed.

i_echo "$TAG Pulling result files"
#adb pull $deviceDir $localDir

adb shell ls "$deviceDir/Measures/" | sed -r 's/[\r]+//g' | egrep "*.csv" |  xargs -I{} adb pull $deviceDir/Measures/{} $localDir
#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
adb shell ls "$deviceDir/Traces/" | sed -r 's/[\r]+//g' | egrep "*.txt" | xargs -I{} adb pull $deviceDir/Traces/{} $localDir

# In case the missing instrumentation error occured, let's remove all apps with instrumentations now!
# if [[ "$flagInst" == 1 ]]; then
	instTests=($(adb shell pm list instrumentation | cut -f2 -d: | cut -f1 -d\ | cut -f1 -d/))
	for i in ${instTests[@]}; do
		a=${i/%.test/}
		adb shell pm uninstall $a
		adb shell pm uninstall $i
		
		a=${i/%.tests/}
		adb shell pm uninstall $a
	done
# fi

exit 0
