#!/bin/bash
TAG="[APP BUILDER]"

OLDIFS=$IFS
IFS=$(echo -en "\n\b")
# $1 - Project ID/PATH/HASH
# $2 - Project package
# $3 - Project source path
# $4 - Project test path

ID=$1
PACKAGE=$2
PROJECT_FOLDER=$3
TEST_FOLDER=$4


BUILD_P=$(find $PROJECT_FOLDER -name "build.xml")
BUILD_T=$(find $TEST_FOLDER -name "build.xml")
echo "$TAG SDK PROJECT"

STATUS_NOK="FAILED"
if [ -n "$BUILD_P" ] && [ -n "$BUILD_T" ]; then
	echo "$TAG Building from existing file"
	ant -f $BUILD_T clean debug &> buildStatus.log
	STATUS_NOK=$(grep "BUILD FAILED" buildStatus.log)
fi
if [ -n "$STATUS_NOK" ]; then
	rm -rf $PROJECT_FOLDER/build.xml $PROJECT_FOLDER/ant.properties $PROJECT_FOLDER/local.properties $PROJECT_FOLDER/project.properties
	rm -rf $TEST_FOLDER/build.xml $TEST_FOLDER/ant.properties $TEST_FOLDER/local.properties $TEST_FOLDER/project.properties
	echo "$TAG Updating Project"
	UPDATE_P=$(android update project -p $PROJECT_FOLDER -t 1 -n Green --subprojects)

	echo "[APP BUILDER] Updating Tests"
	UPDATE_T=$(android update test-project -p $TEST_FOLDER --main $PROJECT_FOLDER)
	ant -f $TEST_FOLDER/build.xml clean debug &> buildStatus.log
fi
IFS=$OLDIFS
#STATUS=$(cat $2/buildStatus.log | tail -n 2 | awk 'NR==1')
STATUS_NOK=$(grep "BUILD FAILED" buildStatus.log)
STATUS_OK=$(grep "BUILD SUCCESS" buildStatus.log)

if [ -n "$STATUS_NOK" ]; then
	echo "$TAG Unable to build project $ID" 
	echo "[ERROR] Aborting"
	exit 1
elif [ -n "$STATUS_OK" ]; then
	echo "$TAG Build successful for project $ID"
else
	echo "$TAG Unable to build project $ID"
	echo "[ERROR] Aborting"
	exit 1
fi
