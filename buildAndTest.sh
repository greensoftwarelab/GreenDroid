#!/bin/bash
source settings.sh

TAG="[GD]"

machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
	SED_COMMAND="gsed" #mac
	MKDIR_COMMAND="gmkdir"
else 
	SED_COMMAND="sed" #linux
	MKDIR_COMMAND="mkdir"	
fi

####################### Method or Test Oriented
TestOriented="ON"   # ON - test oriented | !ON Method Oriented
#######################
if [ $TestOriented == "ON" ]; then 
	trace="-TraceMethods"
else
	trace="wtv"
fi

OLDIFS=$IFS
tName="_TRANSFORMED_"
deviceDir=""
prefix="latest" # "latest" or ""
deviceExternal=""
logDir="logs"
localDir="$HOME/GDResults"
trace="-TraceMethods"   #trace=$2  ##RR
GD_ANALYZER="jars/Analyzer.jar"  # "analyzer/greenDroidAnalyzer.jar"
GD_INSTRUMENT="jars/jInst.jar"
treprefix=""
trepnLib="TrepnLibrary-release.aar"
trepnJar="TrepnLibrary-release.jar"
profileHardware="YES" # YES or ""
flagStatus="on"
SLEEPTIME=1
#DIR=$HOME/tests/wasSuccess/gradleProjects/*



DIR=$HOME/tests/actual/*
#DIR=/Users/ruirua/repos/greenlab-work/work/ruirua/proj/*


#Quickly check the folder containing the apps to be tested for inconsistencies
#if [ "${DIR: -1}" == "*" ]; then
#	TEST_DIR="${DIR:0:-1}"
#else 
#	TEST_DIR=$DIR
#qfi

echo ""
i_echo "### GRENDROID PROFILING TOOL ###     "

if [ ! -d $TEST_DIR ]; then
	e_echo "$TAG Error: Folder $TEST_DIR does not exist"
	exit 1
fi

if [ -z "$(ls -A $TEST_DIR)" ]; then
	e_echo "$TAG Error: Folder $TEST_DIR is empty"
	exit 1
fi

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
	i_echo "$TAG 📲  Attached device recognized"
	#TODO include mode to choose the conected device and echo the device name
	#Start Trepn
	adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
	#put Trepn preferences on device
	(adb push trepnPreferences/ $deviceDir/saved_preferences) > /dev/null  2>&1 #new

	deviceDir="$deviceExternal/trepn"  #GreenDroid
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
	
	#for each Android Proj in $DIR folder...
	w_echo "$TAG searching for Android Projects in -> $DIR"
	for f in $DIR/
		do
		#clean previous list of all methods and device results
		rm -rf ./allMethods.txt

		adb shell rm -rf "$deviceDir/allMethods.txt"
		adb shell rm -rf "$deviceDir/TracedMethods.txt"
		adb shell rm -rf "$deviceDir/Traces/*"
		adb shell rm -rf "$deviceDir/Measures/*"

		IFS='/' read -ra arr <<< "$f"
		#ID=${arr[-1]} # MC
		#IFS=$(echo -en "\n\b") #MC
		ID=${arr[*]: -1}
		IFS=$(echo -en "\n\b")
		now=$(date +"%d_%m_%y_%H_%M_%S")

		if [ "$ID" != "success" ] && [ "$ID" != "failed" ] && [ "$ID" != "unknown" ]; then
			projLocalDir=$localDir/$ID
			rm -rf $projLocalDir/all/*
			if [[ $trace == "-TraceMethods" ]]; then
				w_echo "	Test Oriented Profiling:      ✔"
			else 
				w_echo "	Method Oriented profiling:    ✔"
			fi 
			if [[ $profileHardware == "YES" ]]; then
				w_echo "	Profiling hardware:           ✔"
				(adb shell am broadcast -a com.quicinc.trepn.load_preferences –e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/All.pref") > /dev/null 2>&1
			else 
				(adb shell am broadcast -a com.quicinc.trepn.load_preferences –e com.quicinc.trepn.load_preferences_file "$deviceDir/saved_preferences/trepnPreferences/Pref1.pref") > /dev/null 2>&1
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
						
						FOLDER=${f}${prefix} #$f
						#delete previously instrumented project, if any
						rm -rf $FOLDER/$tName

						#instrument
						#echo "folder of app to instrument ----> $FOLDER"
						echo "$FOLDER/$tName" > lastTranformedApp.txt
						#echo "java -jar jar -gradle _TRANSFORMED_ X $FOLDER $MANIF_S $MANIF_T $trace"
						java -jar $GD_INSTRUMENT "-gradle" $tName "X" $FOLDER $MANIF_S $MANIF_T $trace ##RR
						
						#copy the trace/measure lib
						#folds=($(find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"))
						for D in `find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"`; do  ##RR
						    if [ -d "${D}" ]; then  ##RR
						    	$MKDIR_COMMAND -p ${D}/libs  ##RR
						     	cp libsAdded/$treprefix$trepnLib ${D}/libs  ##RR
						    fi  ##RR
						done  ##RR
		
						#build
						#GRADLE=$(find $FOLDER/$tName -maxdepth 1 -name "build.gradle")
						GRADLE=($(find $FOLDER/$tName -name "*.gradle" -type f -print | grep -v "settings.gradle" | xargs grep -L "com.android.library" | xargs grep -l "buildscript" | cut -f1 -d:))
						#echo "gradle script invocation -> ./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}"
						./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorBuild.log
							if [[ -n "$flagStatus" ]]; then
								cp $logDir/buildStatus.log debugBuild/$ID.log
							fi
							continue
						else 
							i_echo "BUILD SUCCESSFULL"
						fi
						
						#create results support folder
						echo "$TAG Creating support folder..."
						$MKDIR_COMMAND -p $projLocalDir
						$MKDIR_COMMAND -p $projLocalDir/all
						cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
		
						#install on device
						./install.sh $FOLDER/$tName "X" "GRADLE" $PACKAGE $projLocalDir  #COMMENT, EVENTUALLY...
						RET=$(echo $?)
						#if [[ "$RET" != "0" ]]; then
						#	echo "$ID" >> errorInstall.log
						#	continue
						#fi
						echo "$ID" >> $logDir/success.log
						
						#run tests
						./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir # "-gradle" $FOLDER/$tName
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorRun.log
							e_echo "[GD ERROR] There was an Error while running tests. Retrying... "
							#RETRY 
							./trepFix.sh
							adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
							./runTests $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir # "-gradle" $FOLDER/$tName
							RET=$(echo $?)
							if [[ "$RET" != "0" ]]; then
								echo "$ID" >> $logDir/errorRun.log
								e_echo "[GD ERROR] FATAL ERROR RUNNING TESTS. IGNORING APP "
								continue
							fi
						fi
						
						#uninstall the app & tests
						./uninstall.sh $PACKAGE $TESTPACKAGE
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorUninstall.log
							#continue
						fi
						
						#Run greendoid!
						#java -jar $GD_ANALYZER $ID $PACKAGE $TESTPACKAGE $FOLDER $FOLDER/tName $localDir
						(java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv) > $logDir/analyzer.log  ##RR
						cat $logDir/analyzer.log
						errorAnalyzer=$(cat $logDir/analyzer.log)
						#TODO se der erro imprimir a vermelho e aconselhar usar o trepFix.sh
						#break
						./trepnFix.sh
						w_echo "$TAG sleeping between profiling apps"
						sleep 60
						w_echo "$TAG resuming Greendroid after nap"
					done
				fi
			else
				#search for the manifests
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
							java -jar $GD_INSTRUMENT "-sdk" $tName "X" $SOURCE $TESTS $trace
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
							echo "$ID" >> $logDir/errorBuild.log
							if [[ "$RET" == "10" ]]; then
								#everything went well, at second try
								#let's create the results support files
								mkdir -p $projLocalDir
								mkdir -p $projLocalDir/all
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
						$MKDIR_COMMAND -p $projLocalDir/all
						cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
	
						#run tests
						./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorRun.log
							e_echo "[GD ERROR] There was an Error while running tests. Retrying... "
							#RETRY 
							./trepFix.sh
							adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1
							./runTests $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir # "-gradle" $FOLDER/$tName
							RET=$(echo $?)
							if [[ "$RET" != "0" ]]; then
								echo "$ID" >> $logDir/errorRun.log
								e_echo "[GD ERROR] FATAL ERROR RUNNING TESTS. IGNORING APP "
								continue
							fi
						fi
						#uninstall the app & tests
						./uninstall.sh $PACKAGE $TESTPACKAGE
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> $logDir/errorUninstall.log
							#continue
						fi
						#Run greendoid!
						java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv  ##RR
						./trepnFix.sh
						#break
					else
						e_echo "$TAG ERROR!"
					fi
				done
			fi
	    	
	    fi
	done
	IFS=$OLDIFS
	testRes=$(find $projLocalDir -name "Testresults.csv")
	if [ -n $testRes ] ; then 
		cat $projLocalDir/Testresults.csv | $SED_COMMAND 's/,/ ,/g' | column -t -s, | less -S
	fi
	./trepnFix.sh
fi
