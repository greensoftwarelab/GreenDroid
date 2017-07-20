#!/bin/bash
source settings.sh

TAG="[GD]"

OLDIFS=$IFS
tName="_TRANSFORMED_"
deviceDir=""
deviceExternal=""
localDir="$HOME/GDResults"
trace="-TraceMethods"   #trace=$2  ##RR
GD_ANALYZER="analyzer/Analyzer-1.0-SNAPSHOT.jar"  # "analyzer/greenDroidAnalyzer.jar"
trepnLib="TrepnLibrary-release.aar"
trepnJar="TrepnLibrary-release.jar"

#flagStatus="on"

#DIR=/media/data/android_apps/failed/*   #DIR=$1
#DIR=/media/data/android_apps/success/*
DIR=$HOME/tests/androidProjects/*

#Quickly check the folder containing the apps to be tested for inconsistencies
if [ "${DIR: -1}" == "*" ]; then
	TEST_DIR="${DIR:0:-1}"
else 
	TEST_DIR=$DIR
fi


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
	e_echo "$TAG Error: Could not find any attached device. Check and try again..."
else
	deviceExternal=$(adb shell 'echo -n $EXTERNAL_STORAGE')
	if [ -z "$deviceExternal" ]; then
		e_echo "$TAG Could not determine the device's external storage. Check and try again..."
		exit 1
	fi

	#Strat Trepn
	adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1

	deviceDir="$deviceExternal/trepn"  #GreenDroid
	adb shell mkdir $deviceDir
	adb shell rm -rf $deviceDir/Measures/*  ##RR
	adb shell rm -rf $deviceDir/Traces/*  ##RR

	if [[ -n "$flagStatus" ]]; then
		mkdir debugBuild
	fi
	
	#for each app in $DIR folder...
	for f in $DIR/
	do
		#clean previous list of all methods and device results
		rm -rf ./allMethods.txt

		adb shell rm -rf "$deviceDir/allMethods.txt"
		adb shell rm -rf "$deviceDir/TracedMethods.txt"
		adb shell rm -rf "$deviceDir/Traces/*"
		adb shell rm -rf "$deviceDir/Measures/*"

		IFS='/' read -ra arr <<< "$f"
		ID=${arr[-1]}
		IFS=$(echo -en "\n\b")
		if [ "$ID" != "success" ] && [ "$ID" != "failed" ] && [ "$ID" != "unknown" ]; then
			projLocalDir=$localDir/$ID
			rm -rf $projLocalDir/all/*
			#first, check if this is a gradle or a maven project
			#GRADLE=$(find ${f}/latest -maxdepth 1 -name "build.gradle")
			GRADLE=($(find ${f}/latest -name "*.gradle" -type f -print | grep -v "settings.gradle" | xargs -I{} grep "buildscript" {} /dev/null | cut -f1 -d:))
			POM=$(find ${f}/latest -maxdepth 1 -name "pom.xml")
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
						
						FOLDER=${f}/latest #$f
						#delete previously instrumented project, if any
						rm -rf $FOLDER/$tName
						#instrument
						java -jar "jInst/jInst-1.0.jar" "-gradle" $tName "X" $FOLDER $MANIF_S $MANIF_T $trace ##RR
						
						#copy the trace/measure lib
						#folds=($(find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"))
						for D in `find $FOLDER/$tName/ -type d | egrep -v "\/res|\/gen|\/build|\/.git|\/src|\/.gradle"`; do  ##RR
						    if [ -d "${D}" ]; then  ##RR
						    	mkdir -p ${D}/libs  ##RR
						     	cp libsAdded/$trepnLib ${D}/libs  ##RR
						    fi  ##RR
						done  ##RR
		
						#build
						#GRADLE=$(find $FOLDER/$tName -maxdepth 1 -name "build.gradle")
						GRADLE=($(find $FOLDER/$tName -name "*.gradle" -type f -print | grep -v "settings.gradle" | xargs grep -L "com.android.library" | xargs grep -l "buildscript" | cut -f1 -d:))
						./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorBuild.log
							if [[ -n "$flagStatus" ]]; then
								cp buildStatus.log debugBuild/$ID.log
							fi
							continue
						fi
						
						#create results support folder
						echo "$TAG Creating support folder..."
						mkdir -p $projLocalDir
						mkdir -p $projLocalDir/all
						cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
		
						#install on device
						./install.sh $FOLDER/$tName "X" "GRADLE" $PACKAGE $projLocalDir  #COMMENT, EVENTUALLY...
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorInstall.log
							continue
						fi
						echo "$ID" >> success.log
						
						#run tests
						./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir # "-gradle" $FOLDER/$tName
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorRun.log
							continue
						fi
						
						#uninstall the app & tests
						./uninstall.sh $PACKAGE $TESTPACKAGE
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorUninstall.log
							#continue
						fi
						
						#Run greendoid!
						#java -jar $GD_ANALYZER $ID $PACKAGE $TESTPACKAGE $FOLDER $FOLDER/tName $localDir
						java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv  ##RR
						#break
					done
				fi
			else
				#search for the manifests
				MANIFESTS=($(find $f -name "AndroidManifest.xml" | egrep -v "/bin/|$tName"))
				MP=($(python manifestParser.py ${MANIFESTS[*]}))
				for R in ${MP[@]}; do
					RESULT=($(echo "$R" | tr ':' '\n'))
					SOURCE=${RESULT[0]}
					TESTS=${RESULT[1]}
					PACKAGE=${RESULT[2]}
					TESTPACKAGE=${RESULT[3]}
					if [ "$SOURCE" != "" ] && [ "$TESTS" != "" ] && [ "$f" != "" ]; then
						#delete previously instrumented project, if any
						rm -rf $SOURCE/$tName
						#instrument
						if [[ "$SOURCE" != "$TESTS" ]]; then
							java -jar "jInst/jInst-1.0.jar" "-sdk" $tName "X" $SOURCE $TESTS $trace
						else
							MANIF_S="${SOURCE}/AndroidManifest.xml"
							MANIF_T="-"
							java -jar "jInst/jInst-1.0.jar" "-gradle" $tName "X" $SOURCE $MANIF_S $MANIF_T $trace ##RR
						fi

						#copy the test runner
						mkdir $SOURCE/$tName/libs
						mkdir $SOURCE/$tName/tests/libs
						cp libsAdded/$trepnJar $SOURCE/$tName/libs
						cp libsAdded/$trepnJar $SOURCE/$tName/tests/libs
	
						#build
						./buildSDK.sh $ID $PACKAGE $SOURCE/$tName $SOURCE/$tName/tests $deviceDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorBuild.log
							if [[ "$RET" == "10" ]]; then
								#everything went well, at second try
								#let's create the results support files
								mkdir -p $projLocalDir
								mkdir -p $projLocalDir/all
								cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
								echo "$ID" >> success.log
							elif [[ -n "$flagStatus" ]]; then
								cp buildStatus.log debugBuild/$ID.log
							fi
							continue
						fi
						
						#install on device
						./install.sh $SOURCE/$tName $SOURCE/$tName/tests "SDK" $PACKAGE $localDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorInstall.log
							continue
						fi
						echo "$ID" >> success.log
	
						#create results support folder
						echo "$TAG Creating support folder..."
						mkdir -p $projLocalDir
						mkdir -p $projLocalDir/all
						cat ./allMethods.txt >> $projLocalDir/all/allMethods.txt
	
						#run tests
						./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorRun.log
							continue
						fi
						#uninstall the app & tests
						./uninstall.sh $PACKAGE $TESTPACKAGE
						RET=$(echo $?)
						if [[ "$RET" != "0" ]]; then
							echo "$ID" >> errorUninstall.log
							#continue
						fi
						#Run greendoid!
						java -jar $GD_ANALYZER $trace $projLocalDir/ $projLocalDir/all/ $projLocalDir/*.csv  ##RR
						#break
					else
						e_echo "$TAG ERROR!"
					fi
				done
			fi
	    	
	    fi
	done
	IFS=$OLDIFS
fi
