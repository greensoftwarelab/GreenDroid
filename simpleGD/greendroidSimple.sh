#!/bin/bash
source settings.sh

DIR=$1
trepnLib="TrepnLibrary-release.aar"
TAG="[GD]"
profileHardware="YES" # YES or ""
localDir="GDResults"
trace="-TraceMethods" 
deviceDir=""
deviceExternal=""
GD_ANALYZER="Analyzer.jar"
adb kill-server
DEVICE=$(adb devices -l | egrep "device .+ product:")
if [ -z "$DEVICE" ]; then
	e_echo "$TAG Error: Could not find any attached device. Check and try again..."
else
	echo "Device Detected"
	deviceExternal=$(adb shell 'echo -n $EXTERNAL_STORAGE')
	if [ -z "$deviceExternal" ]; then
		e_echo "$TAG Could not determine the device's external storage. Check and try again..."
		exit 1
	fi

	adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
	deviceDir="$deviceExternal/trepn"  #GreenDroid
	adb shell mkdir $deviceDir
	adb shell mkdir $deviceDir/saved_preferences #new
	(adb push trepnPreferences/ $deviceDir/saved_preferences) > /dev/null  2>&1 #new
	#remove old measures
	adb shell rm -rf $deviceDir/Measures/*  ##RR
	adb shell rm -rf $deviceDir/Traces/*  ##RR
	adb shell rm -rf "$deviceDir/allMethods.txt"  ##RR
	adb shell rm -rf "$deviceDir/Traces/*"  ##RR
	adb shell rm -rf "$deviceDir/TracedMethods.txt"  ##RR
	adb shell rm -rf "$deviceDir/Measures/*"  ##RR
	

	if [[ -n "$profileHardware" ]]; then
		adb shell am broadcast -a com.quicinc.trepn.load_preferences –e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/All.pref"
	else 
		adb shell am broadcast -a com.quicinc.trepn.load_preferences –e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/Pref1.pref"
	fi

	echo "$TAG Running the tests (Measuring)"
	adb shell "echo 1 > $deviceDir/GDflag"
	chmod +x $DIR/gradlew
	ACTUALDIR=($(pwd ))
	cd $DIR ; ./gradlew cAT;
	cd $ACTUALDIR
	#echo "$TAG Running the tests (Tracing)"
	#adb shell "echo -1 > $deviceDir/GDflag"
	#$DIR/gradlew cAT
	mkdir -p $localDir
	mkdir -p $localDir/oldRuns
	IFS='/' read -ra arr <<< "$DIR"
	ID=${arr[*]: -1}
	echo "ID -> $ID"
	IFS=$(echo -en "\n\b")
	now=$(date +"%d_%m_%y_%H_%M_%S")
	find $localDir/ -maxdepth 1 -mindepth 1 -not -name oldRuns |  xargs -I{} mv {} $localDir/oldRuns/
	projLocalDir=$localDir/$ID$now
	echo "$TAG Creating support folder..."
	mkdir -p $projLocalDir
	mkdir -p $projLocalDir/all
	#pull results
	adb shell ls "$deviceDir/Measures/" | gsed -r 's/[\r]+//g' | egrep "*.csv" |  xargs -I{} adb pull $deviceDir/Measures/{} $projLocalDir
	#since there is no need to trace.
	echo "" > $projLocalDir/all/allMethods.txt
	#analyze results
	java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv 
	#pretty print
	cat $projLocalDir/Testresults.csv | gsed 's/,/ ,/g' | column -t -s, | less -S
fi




