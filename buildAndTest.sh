#!/bin/bash
TAG="[GD]"

OLDIFS=$IFS
tName="_TRANSFORMED_"
deviceDir=""
deviceExternal=""
localDir="/home/marco/GDResults"
trace=$1  ##RR
GD_ANALYZER="analyzer/Analyzer-1.0-SNAPSHOT.jar"  # "analyzer/greenDroidAnalyzer.jar"
trepnLib="TrepnLibrary-release.aar"

DIR=/home/marco/tests/androidProjects/testproj/*
#Normally, the vars TESTS_SRC and f would already been setted
#TESTS_SRC=...
#f=...

adb kill-server
DEVICE=$(adb devices -l | egrep "device .+ product:")
if [ -z "$DEVICE" ]; then
	echo "$TAG Error: Could not find any attached device. Check and try again..."
else
	deviceExternal=$(adb shell 'echo -n $EXTERNAL_STORAGE')
	if [ -z "$deviceExternal" ]; then
		echo "$TAG Could not determine the device's external storage. Check and try again..."
		exit
	fi

	#Strat Trepn
	adb shell monkey -p com.quicinc.trepn -c android.intent.category.LAUNCHER 1

	deviceDir=$deviceExternal/trepn  #GreenDroid
	adb shell mkdir $deviceDir
	adb shell rm -rf $deviceDir/*.csv  ##RR
	adb shell rm -rf $deviceDir/Traces/*  ##RR
	#for each app in $DIR folder...
	for f in $DIR/
	do
		IFS='/' read -ra arr <<< "$f"
		ID=${arr[-1]}
		IFS=$(echo -en "\n\b")
		if [ "$ID" != "success" ] && [ "$ID" != "failed" ] && [ "$ID" != "unknown" ]; then
			#first, check if this is a gradle or a maven project
			#GRADLE=$(find ${f}/latest -maxdepth 1 -name "build.gradle")
			GRADLE=($(find ${f}/latest -name "build.gradle" -print | xargs -I{} grep "buildscript" {} /dev/null | cut -f1 -d:))
			POM=$(find ${f}/latest -maxdepth 1 -name "pom.xml")
			if [ -n "$POM" ]; then
				POM=${POM// /\\ }
				# Maven porjects are not considered yet...
			elif [ -n "${GRADLE[0]}" ]; then
				MANIFESTS=($(find $f -name "AndroidManifest.xml" | egrep -v "/build/|$tName"))
				if [[ "${#MANIFESTS[@]}" > 1 ]]; then
					RESULT=($(python manifestParser.py ${MANIFESTS[*]}))
					TESTS_SRC=${RESULT[1]}
					PACKAGE=${RESULT[2]}
					TESTPACKAGE=${RESULT[3]}
				else
					MANIF_S=${MANIFESTS[0]}
					MANIF_T="-"
					PACKS=($(./searchPackage.sh $f))
					PACKAGE=${PACKS[0]}
					TESTPACKAGE=${PACKS[1]}
					#this line will be necessary only now
					TESTS_SRC=""
				fi
				FOLDER=${f}/latest #$f
				#delete previously instrumented project, if any
				rm -rf $FOLDER/$tName
				#instrument
				java -jar "jInst/jInst-1.0.jar" "-gradle" $tName "X" $FOLDER $MANIF_S $MANIF_T $trace ##RR
				exit 0
				#copy the test runner
				for D in `find $FOLDER$tName/ -maxdepth 2 -type d`; do  ##RR
				    if [ -d "${D}" ]; then  ##RR
				      mkdir ${D}/libs  ##RR
				      cp libsAdded/$trepnLib $ ${D}/libs  ##RR
				    fi  ##RR
				done  ##RR

				#build
				#GRADLE=$(find $FOLDER/$tName -maxdepth 1 -name "build.gradle")
				GRADLE=($(find $FOLDER/$tName -name "build.gradle" -print | xargs grep "buildscript" | cut -f1 -d:))
				./buildGradle.sh $ID $FOLDER/$tName ${GRADLE[0]}
				RET=$(echo $?)
				if [[ "$RET" != "0" ]]; then
					break
				fi
				#install on device
				./install.sh $FOLDER/$tName "X" "GRADLE" $PACKAGE $localDir  #COMMENT, EVENTUALLY...
				RET=$(echo $?)
				if [[ "$RET" != "0" ]]; then
					break
				fi
				#run tests
				projLocalDir=$localDir/$ID
				mkdir -p $projLocalDir
				./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir # "-gradle" $FOLDER/$tName
				RET=$(echo $?)
				if [[ "$RET" != "0" ]]; then
					break
				fi
				#uninstall the app & tests
				./uninstall.sh $PACKAGE $TESTPACKAGE
				RET=$(echo $?)
				if [[ "$RET" != "0" ]]; then
					break
				fi
				#Run greendoid!
				#java -jar $GD_ANALYZER $ID $PACKAGE $TESTPACKAGE $FOLDER $FOLDER/tName $localDir
				java -jar $GD_ANALYZER $trace $projLocalDir/ *.csv  ##RR
				#break
			else
				#search for the manifests
				MANIFESTS=($(find $f -name "AndroidManifest.xml" | egrep -v "/bin/|$tName"))
				RESULT=($(python manifestParser.py ${MANIFESTS[*]}))
				SOURCE=${RESULT[0]}
				TESTS=${RESULT[1]}
				PACKAGE=${RESULT[2]}
				TESTPACKAGE=${RESULT[3]}
				if [ "$SOURCE" != "" ] && [ "$TESTS" != "" ] && [ "$f" != "" ]; then
					#delete previously instrumented project, if any
					rm -rf $SOURCE/$tName
					#instrument
					java -jar "jInst/jInst-1.0.jar" -sdk $tName "X" $SOURCE $TESTS
					#copy the test runner
					mkdir $SOURCE/$tName/libs
					mkdir $SOURCE/$tName/tests/libs
					cp libsAdded/$trepnLib $SOURCE/$tName/libs
					cp libsAdded/$trepnLib $SOURCE/$tName/tests/libs

					#build
					./buildSDK.sh $ID $PACKAGE $SOURCE/$tName $SOURCE/$tName/tests
					RET=$(echo $?)
					if [[ "$RET" != "0" ]]; then
						break
					fi
					#install on device
					./install.sh $SOURCE/$tName $SOURCE/$tName/tests "SDK" $PACKAGE $localDir
					RET=$(echo $?)
					if [[ "$RET" != "0" ]]; then
						break
					fi
					#run tests
					projLocalDir=$localDir/$ID
					mkdir -p $projLocalDir
					./runTests.sh $PACKAGE $TESTPACKAGE $deviceDir $projLocalDir
					RET=$(echo $?)
					if [[ "$RET" != "0" ]]; then
						break
					fi
					#uninstall the app & tests
					./uninstall.sh $PACKAGE $TESTPACKAGE
					RET=$(echo $?)
					if [[ "$RET" != "0" ]]; then
						break
					fi
					#Run greendoid!
					java -jar $GD_ANALYZER $trace $projLocalDir/ *.csv  ##RR
					#break
				else
					echo "$TAG ERROR!"
				fi
			fi
	    	
	    fi
	done
	IFS=$OLDIFS
fi
