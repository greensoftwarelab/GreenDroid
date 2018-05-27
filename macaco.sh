#!/bin/bash
source settings.sh

TAG="[GD]"

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

OLDIFS=$IFS
tName="_TRANSFORMED_"
deviceDir=""
prefix="latest" # "latest" or "" ; Remove if normal app
deviceExternal=""
logDir="logs"
localDir="$HOME/GDResults"
#trace="-MethodOriented"   #trace=$2  ##RR
trace="-TestOriented"
monkey="-Monkey"
folderPrefix=""
GD_ANALYZER="jars/Analyzer.jar"  # "analyzer/greenDroidAnalyzer.jar"
GD_INSTRUMENT="jars/jInst.jar"
trepnLib="TrepnLibrary-release.aar"
trepnJar="TrepnLibrary-release.jar"
profileHardware="YES" # YES or ""
flagStatus="on"
#SLEEPTIME=180 # 3 minutes
SLEEPTIME=1
min_monkey_runs=3
threshold_monkey_runs=50
number_monkey_events=1000
min_coverage=10
totaUsedTests=0

#DIR=/media/data/android_apps/23Apps/*
DIR=$HOME/tests/actual/*
#DIR=/Users/ruirua/repos/greenlab-work/work/ruirua/proj/*


echo   "################################"
i_echo "### GRENDROID PROFILING TOOL ###     "

adb kill-server
DEVICE=$(adb devices -l | egrep "device .+ product:")
if [ -z "$DEVICE" ]; then
	e_echo "$TAG Error: 📵 Could not find any attached device. Check and try again..."
else
	deviceExternal=$(adb shell 'echo -n $EXTERNAL_STORAGE')
	if [ -z "$deviceExternal" ]; then
		e_echo "$TAG Could not determine the device's external storage. Check and try again..."
		exit 1
	fi
	( adb devices -l ) > device_info.txt
	device_model=$(   cat device_info.txt | grep -o "model.*" | cut -f2 -d: | cut -f1 -d\ )
	device_serial=$(  cat device_info.txt | tail -n 2 | grep "model" | cut -f1 -d\ )
	device_brand=$( cat device_info.txt | grep -o "device:.*" | cut -f2 -d: )
	echo "{\"device_serial_number\": \"$device_serial\", \"device_model\": \"$device_model\",\"device_brand\": \"$device_brand\"}" > device.json
	cat device.json
	#device=$( adb devices -l | grep -o "model.*" | cut -f2 -d: | cut -f1 -d\ )
	i_echo "$TAG 📲  Attached device ($device_model) recognized "
	#TODO include mode to choose the conected device and echo the device name
	deviceDir="$deviceExternal/trepn"  #GreenDroid
	#put Trepn preferences on device
	(adb push trepnPreferences/ $deviceDir/saved_preferences/) > /dev/null  2>&1 #new
	#Start Trepn
	#adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
	
	adb shell am startservice --user 0 com.quicinc.trepn/.TrepnService
	
	(echo $deviceDir > deviceDir.txt) 
	(adb shell mkdir $deviceDir) > /dev/null  2>&1
	(adb shell mkdir $deviceDir/Traces) > /dev/null  2>&1
	(adb shell mkdir $deviceDir/Measures) > /dev/null  2>&1
	(adb shell mkdir $deviceDir/TracedTests) > /dev/null  2>&1
	adb shell rm -rf $deviceDir/Measures/*  ##RR
	adb shell rm -rf $deviceDir/Traces/*  ##RR
	adb shell rm -rf $deviceDir/TracedTests/*  ##RR

	if [[ -n "$flagStatus" ]]; then
		($MKDIR_COMMAND debugBuild ) > /dev/null  2>&1 #new

	fi
	w_echo "removing old instrumentations "
	./forceUninstall.sh
	#for each Android Proj in $DIR folder...
	w_echo "$TAG searching for Android Projects in -> $DIR"


	# getting all seeds from file
	seeds20=$(head -$min_monkey_runs monkey_seeds.txt)
	last30=$(tail  -30 monkey_seeds.txt)


	for f in $DIR/
		do
		#clean previous list of all methods and device results
		rm -rf ./allMethods.txt

		adb shell rm -rf "$deviceDir/allMethods.txt"
		adb shell rm -rf "$deviceDir/TracedMethods.txt"
		adb shell rm -rf "$deviceDir/Traces/*"
		adb shell rm -rf "$deviceDir/Measures/*"
		adb shell rm -rf "$deviceDir/TracedTests/*"  ##RR

		IFS='/' read -ra arr <<< "$f"
		#ID=${arr[-1]} # MC
		#IFS=$(echo -en "\n\b") #MC
		ID=${arr[*]: -1}
		IFS=$(echo -en "\n\b")
		now=$(date +"%d_%m_%y_%H_%M_%S")

		if [ "$ID" != "success" ] && [ "$ID" != "failed" ] && [ "$ID" != "unknown" ]; then
			projLocalDir=$localDir/$ID
			#rm -rf $projLocalDir/all/*
			if [[ $trace == "-TestOriented" ]]; then
				e_echo "	Test Oriented Profiling:      ✔"
				folderPrefix="MonkeyTest"
			else 
				e_echo "	Method Oriented profiling:    ✔"
				folderPrefix="MonkeyMethod"
			fi 
			if [[ $profileHardware == "YES" ]]; then
				w_echo "	Profiling hardware:           ✔"
				#(adb shell am broadcast -a com.quicinc.trepn.load_preferences –e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/All.pref") > /dev/null 2>&1
				(adb shell am broadcast -a com.quicinc.trepn.load_preferences -e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/All.pref") > /dev/null 2>&1
			else 
				(adb shell am broadcast -a com.quicinc.trepn.load_preferences -e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/Pref1.pref") > /dev/null 2>&1
			fi
		
			#first, check if this is a gradle or a maven project
			#GRADLE=$(find ${f}/latest -maxdepth 1 -name "build.gradle")
			GRADLE=($(find ${f}/${prefix} -name "*.gradle" -type f -print | grep -v "settings.gradle" | xargs -I{} grep "buildscript" {} /dev/null | cut -f1 -d:))
			POM=$(find ${f}/${prefix} -maxdepth 1 -name "pom.xml")
			if [ -n "$POM" ]; then
				POM=${POM// /\\ }
				# Maven porjects are not considered yet...
			elif [ -n "${GRADLE[0]}" ]; then
				MANIFESTS=($(find $f -name "AndroidManifest.xml" | egrep -v "/build/|$tName"))
				if [[ "${#MANIFESTS[@]}" > 0 ]]; then
					MP=($(python manifestParser.py ${MANIFESTS[*]}))
					for R in ${MP[@]}; do
						RESULT=($(echo "$R" | tr ':' '\n'))
						TESTS_SRC=${RESULT[1]}
						PACKAGE=${RESULT[2]}
						if [[ "${RESULT[3]}" != "-" ]]; then
							TESTPACKAGE=${RESULT[3]}
						else
							TESTPACKAGE="$PACKAGE.test"
						fi
						MANIF_S="${RESULT[0]}/AndroidManifest.xml"
						MANIF_T="-"
						


#create results support folder
						echo "$TAG Creating support folder..."
						$MKDIR_COMMAND -p $projLocalDir
						$MKDIR_COMMAND -p $projLocalDir/oldRuns
						$MV_COMMAND -f $(find  $projLocalDir/ -maxdepth 1 | $SED_COMMAND -n '1!p' |grep -v "oldRuns") $projLocalDir/oldRuns/
						$MKDIR_COMMAND -p $projLocalDir/all

						FOLDER=${f}${prefix} #$f
						#delete previously instrumented project, if any
		
#Instrumentation phase	

						oldInstrumentation=$(cat $FOLDER/$tName/instrumentationType.txt | grep  ".*Oriented" )
						allmethods=$(find $projLocalDir/all -maxdepth 1 -name "allMethods.txt")
						if [ "$oldInstrumentation" != "$trace" ] || [ -z "$allmethods" ]; then
							w_echo "Different type of instrumentation. instrumenting again..."
							rm -rf $FOLDER/$tName
							java -jar $GD_INSTRUMENT "-gradle" $tName "X" $FOLDER $MANIF_S $MANIF_T $trace $monkey ##RR
							#create results support folder
							#rm -rf $projLocalDir/all/*
							$MV_COMMAND ./allMethods.txt $projLocalDir/all/allMethods.txt
							#$MV_COMMAND ./allMethods.txt $projLocalDir/all/allMethods.txt
						else 
							w_echo "Same instrumentation of last time. Skipping instrumentation phase"
						fi
						
						(echo "{\"app_id\": \"$ID\", \"app_location\": \"$f\",\"app_build_tool\": \"gradle\", \"app_version\": \"1\", \"app_language\": \"Java\"}") > $FOLDER/$tName/application.json
						xx=$(find  $projLocalDir/ -maxdepth 1 | $SED_COMMAND -n '1!p' |grep -v "oldRuns" | grep -v "all" )
						##echo "xx -> $xx"
						$MV_COMMAND -f $xx $projLocalDir/oldRuns/
						echo "$FOLDER/$tName" > lastTranformedApp.txt

						#copy the trace/measure lib
						#folds=($(find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"))
						for D in `find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"`; do  ##RR
						    if [ -d "${D}" ]; then  ##RR
						    	$MKDIR_COMMAND -p ${D}/libs  ##RR
						     	cp libsAdded/$treprefix$trepnLib ${D}/libs  ##RR
						    fi  ##RR
						done  ##RR
		

## BUILD PHASE						

						GRADLE=($(find $FOLDER/$tName -name "*.gradle" -type f -print | grep -v "settings.gradle" | xargs grep -L "com.android.library" | xargs grep -l "buildscript" | cut -f1 -d:))
						#echo "gradle script invocation -> ./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}"
						if [ "$oldInstrumentation" != "$trace" ] || [ -z "$allmethods" ]; then
							w_echo "[APP BUILDER] Different instrumentation since last time. Building Again"
							./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}
						else 
							w_echo "[APP BUILDER] No changes since last run. Not building again"
						fi
						(echo $trace) > $FOLDER/$tName/instrumentationType.txt
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorBuildGradle.log
							cp $logDir/buildStatus.log $f/buildStatus.log
							if [[ -n "$flagStatus" ]]; then
								cp $logDir/buildStatus.log debugBuild/$ID.log
							fi
							continue
						else 
							i_echo "BUILD SUCCESSFULL"
						fi
## END BUILD PHASE					
						
						
						
						##copy MethodMetric to support folder
						#echo "copiar $FOLDER/$tName/classInfo.ser para $projLocalDir "
						cp $FOLDER/$tName/AppInfo.ser $projLocalDir
						cp device.json $localDir
						cp $FOLDER/$tName/appPermissions.json $localDir

						#install on device
						./install.sh $FOLDER/$tName "X" "GRADLE" $PACKAGE $projLocalDir  #COMMENT, EVENTUALLY...
						RET=$(echo $?)
						#if [[ "$RET" != "0" ]]; then
						#	echo "$ID" >> errorInstall.log
						#	continue
						#fi
						echo "$ID" >> $logDir/success.log
						total_methods=$( cat $projLocalDir/all/allMethods.txt | sort -u | wc -l | $SED_COMMAND 's/ //g')
						now=$(date +"%d_%m_%y_%H_%M_%S")
						localDir=$localDir/$ID/$folderPrefix$now
						echo "$TAG Creating support folder..."
						mkdir -p $localDir
						mkdir -p $localDir/all
						##########
########## RUN TESTS 1 phase ############

						for i in $seeds20; do
							w_echo "SEED Number : $totaUsedTests"
							./runMonkeyTest.sh $i $number_monkey_events $trace $PACKAGE				
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.csv" |  xargs -I{} adb pull $deviceDir/{} $localDir
							#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio "TracedMethods.txt" | xargs -I{} adb pull $deviceDir/{} $localDir
							mv $localDir/TracedMethods.txt $localDir/TracedMethods$i.txt
							mv $localDir/GreendroidResultTrace0.csv $localDir/GreendroidResultTrace$i.csv
							totaUsedTests=$(($totaUsedTests + 1))
						done

########## RUN TESTS  THRESHOLD ############

						##check if have enough coverage
						nr_methods=$( cat $localDir/Traced*.txt | sort -u | wc -l | $SED_COMMAND 's/ //g')
						actual_coverage=$(echo "${nr_methods}/${total_methods}" | bc -l)
						e_echo "actual coverage -> $actual_coverage"
						
						for j in $last30; do
							coverage_exceded=$( echo " ${actual_coverage}>= .${min_coverage}" | bc -l)
							if [ "$coverage_exceded" -gt 0 ]; then
								echo "$ID|$totaUsedTests" >> $logDir/above$min_coverage.log
								break
							fi
							w_echo "SEED Number : $totaUsedTests"
							./runMonkeyTest.sh $j $number_monkey_events $trace $PACKAGE				
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.csv" |  xargs -I{} adb pull $deviceDir/{} $localDir
							#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio "TracedMethods.txt" | xargs -I{} adb pull $deviceDir/{} $localDir
							mv $localDir/TracedMethods.txt $localDir/TracedMethods$i.txt
							mv $localDir/GreendroidResultTrace0.csv $localDir/GreendroidResultTrace$i.csv
							nr_methods=$( cat $localDir/Traced*.txt | sort -u | wc -l | $SED_COMMAND 's/ //g')
							actual_coverage=$(echo "${nr_methods}/${total_methods}" | bc -l)
							w_echo "actual coverage -> $actual_coverage"
							totaUsedTests=$(($totaUsedTests + 1))
						done

						if [ "$coverage_exceded" -eq 0 ]; then
							echo "$ID|$actual_coverage" >> $logDir/below$min_coverage.log
						fi


						(echo "{\"app_id\": \"$ID\", \"app_location\": \"$f\",\"app_build_tool\": \"gradle\", \"app_version\": \"1\", \"app_language\": \"Java\"}") > $localDir/application.json
						(echo "{\"device_serial_number\": \"$device_serial\", \"device_model\": \"$device_model\",\"device_brand\": \"$device_brand\"}") > device.json
						./forceUninstall.sh 
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorUninstall.log
							#continue
						fi
						#Run greendoid!
						#java -jar $GD_ANALYZER $ID $PACKAGE $TESTPACKAGE $FOLDER $FOLDER/tName $localDir
						#(java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv) > $logDir/analyzer.log  ##RR
						w_echo "Analyzing results .."
						java -jar $GD_ANALYZER $trace $projLocalDir/ $monkey
						#cat $logDir/analyzer.log
						errorAnalyzer=$(cat $logDir/analyzer.log)
						#TODO se der erro imprimir a vermelho e aconselhar usar o trepFix.sh
						#break
						w_echo "$TAG sleeping between profiling apps"
						sleep $SLEEPTIME
						w_echo "$TAG resuming Greendroid after nap"
						totaUsedTests=0
						sleep $SLEEPTIME
						battery_level=$(adb shell dumpsys battery | grep -o "level.*" | cut -f2 -d: | $SED_COMMAND 's/ //g')
						w_echo " Actual battery level : $battery_level"
						if [ "$battery_level" -le 20 ]; then
							w_echo "battery level below 20%. Sleeping again"
							sleep $SLEEPTIME # sleep again to charge battery
						fi
					done
				fi
			else
#SDK PROJ
				MANIFESTS=($(find $f -name "AndroidManifest.xml" | egrep -v "/bin/|$tName"))
				MP=($(python manifestParser.py ${MANIFESTS[*]}))
				for R in ${MP[@]}; do
					RESULT=($(echo "$R" | tr ':' '\n'))
					echo "result -> $RESULT"
					SOURCE=${RESULT[0]}
					TESTS=${RESULT[1]}
					PACKAGE=${RESULT[2]}
					TESTPACKAGE=${RESULT[3]}
					if [ "$SOURCE" != "" ] && [ "$TESTS" != "" ] && [ "$f" != "" ]; then
						#delete previously instrumented project, if any
						rm -rf $SOURCE/$tName
						#instrument
						if [[ "$SOURCE" != "$TESTS" ]]; then
							java -jar $GD_INSTRUMENT "-sdk" $tName "X" $SOURCE $TESTS $trace $monkey
						else
							MANIF_S="${SOURCE}/AndroidManifest.xml"
							MANIF_T="-"
							java -jar $GD_INSTRUMENT "-gradle" $tName "X" $SOURCE $MANIF_S $MANIF_T $trace ##RR
						fi

						#copy the test runner
						$MKDIR_COMMAND -p $SOURCE/$tName/libs
						$MKDIR_COMMAND -p $SOURCE/$tName/tests/libs
						cp libsAdded/$trepnJar $SOURCE/$tName/libs
						cp libsAdded/$trepnJar $SOURCE/$tName/tests/libs
	
						#build
						./buildSDK.sh $ID $PACKAGE $SOURCE/$tName $SOURCE/$tName/tests $deviceDir $localDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorBuildSDK.log
							if [[ "$RET" == "10" ]]; then
								#everything went well, at second try
								#let's create the results support files
								$MKDIR_COMMAND -p $projLocalDir
								$MKDIR_COMMAND -p $projLocalDir/oldRuns
								mv  $(ls $projLocalDir | grep -v "oldRuns") $projLocalDir/oldRuns/
								$MKDIR_COMMAND -p $projLocalDir/all
								cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
								echo "$ID" >> $logDir/success.log
							elif [[ -n "$flagStatus" ]]; then
								cp $logDir/buildStatus.log debugBuild/$ID.log
							fi
							continue
						fi
						
						#install on device
						./install.sh $SOURCE/$tName $SOURCE/$tName/tests "SDK" $PACKAGE $localDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorInstall.log
							continue
						fi
						echo "$ID" >> $logDir/success.log
	
						#create results support folder
						echo "$TAG Creating support folder..."
						$MKDIR_COMMAND -p $projLocalDir
						$MKDIR_COMMAND -p $projLocalDir/oldRuns
						$MV_COMMAND -f $(find  $projLocalDir/ -maxdepth 1 | $SED_COMMAND -n '1!p' |grep -v "oldRuns") $projLocalDir/oldRuns/
						$MKDIR_COMMAND -p $projLocalDir/all
						cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
						
						##copy MethodMetric to support folder
						#echo "copiar $FOLDER/$tName/classInfo.ser para $projLocalDir "
						cp $FOLDER/$tName/AppInfo.ser $projLocalDir
						
########## RUN TESTS 1 phase ############

						for i in $seeds20; do
							w_echo "SEED Number : $totaUsedTests"
							./runMonkeyTest.sh $i $number_monkey_events $trace $PACKAGE				
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.csv" |  xargs -I{} adb pull $deviceDir/{} $localDir
							#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio "TracedMethods.txt" | xargs -I{} adb pull $deviceDir/{} $localDir
							mv $localDir/TracedMethods.txt $localDir/TracedMethods$i.txt
							mv $localDir/GreendroidResultTrace0.csv $localDir/GreendroidResultTrace$i.csv
							totaUsedTests=$(($totaUsedTests + 1))
						done

########## RUN TESTS  THRESHOLD ############

						##check if have enough coverage
						nr_methods=$( cat $localDir/Traced*.txt | sort -u | wc -l | $SED_COMMAND 's/ //g')
						actual_coverage=$(echo "${nr_methods}/${total_methods}" | bc -l)
						e_echo "actual coverage -> $actual_coverage"
						
						for j in $last30; do
							coverage_exceded=$( echo " ${actual_coverage}>= .${min_coverage}" | bc -l)
							if [ "$coverage_exceded" -gt 0 ]; then
								echo "$ID|$totaUsedTests" >> $logDir/above$min_coverage.log
								break
							fi
							w_echo "SEED Number : $totaUsedTests"
							./runMonkeyTest.sh $j $number_monkey_events $trace $PACKAGE				
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio ".*.csv" |  xargs -I{} adb pull $deviceDir/{} $localDir
							#adb shell ls "$deviceDir/TracedMethods.txt" | tr '\r' ' ' | xargs -n1 adb pull 
							adb shell ls "$deviceDir" | $SED_COMMAND -r 's/[\r]+//g' | egrep -Eio "TracedMethods.txt" | xargs -I{} adb pull $deviceDir/{} $localDir
							mv $localDir/TracedMethods.txt $localDir/TracedMethods$i.txt
							mv $localDir/GreendroidResultTrace0.csv $localDir/GreendroidResultTrace$i.csv
							nr_methods=$( cat $localDir/Traced*.txt | sort -u | wc -l | $SED_COMMAND 's/ //g')
							actual_coverage=$(echo "${nr_methods}/${total_methods}" | bc -l)
							w_echo "actual coverage -> $actual_coverage"
							totaUsedTests=$(($totaUsedTests + 1))
						done

						if [ "$coverage_exceded" -eq 0 ]; then
							echo "$ID|$actual_coverage" >> $logDir/below$min_coverage.log
						fi


						(echo "{\"app_id\": \"$ID\", \"app_location\": \"$f\",\"app_build_tool\": \"gradle\", \"app_version\": \"1\", \"app_language\": \"Java\"}") > $localDir/application.json
						(echo "{\"device_serial_number\": \"$device_serial\", \"device_model\": \"$device_model\",\"device_brand\": \"$device_brand\"}") > device.json
						./forceUninstall.sh 
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorUninstall.log
							#continue
						fi
						#Run greendoid!
						#java -jar $GD_ANALYZER $ID $PACKAGE $TESTPACKAGE $FOLDER $FOLDER/tName $localDir
						#(java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv) > $logDir/analyzer.log  ##RR
						w_echo "Analyzing results .."
						java -jar $GD_ANALYZER $trace $projLocalDir/ $monkey
						#cat $logDir/analyzer.log
						errorAnalyzer=$(cat $logDir/analyzer.log)
						#TODO se der erro imprimir a vermelho e aconselhar usar o trepFix.sh
						#break
						w_echo "$TAG sleeping between profiling apps"
						sleep $SLEEPTIME
						w_echo "$TAG resuming Greendroid after nap"
						totaUsedTests=0
						sleep $SLEEPTIME
						battery_level=$(adb shell dumpsys battery | grep -o "level.*" | cut -f2 -d: | $SED_COMMAND 's/ //g')
						w_echo " Actual battery level : $battery_level"
						if [ "$battery_level" -le 20 ]; then
							w_echo "battery level below 20%. Sleeping again"
							sleep $SLEEPTIME # sleep again to charge battery
						fi
					else
						e_echo "$TAG ERROR!"
					fi
				done
			fi
	    fi
	done
	IFS=$OLDIFS
#	testRes=$(find $projLocalDir -name "Testresults.csv")
#	if [ -n $testRes ] ; then 
#		cat $projLocalDir/Testresults.csv | $SED_COMMAND 's/,/ ,/g' | column -t -s, | less -S
#	fi
	#./trepnFix.sh
fi



