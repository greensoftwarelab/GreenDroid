#!/bin/bash
source settings.sh


OLDIFS=$IFS
IFS=$(echo -en "\n\b")
# $1 - Project ID/PATH/HASH
# $2 - Project package
# $3 - Project source path
# $4 - Project test path

machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
	SED_COMMAND="gsed" #mac
else 
	SED_COMMAND="sed" #linux	
fi

ID=$1
PACKAGE=$2
PROJECT_FOLDER=$3
TEST_FOLDER=$4

deviceDir=$5
localDir=$6

TAG="[APP BUILDER]"
echo ""

logDir="logs"
BUILD_P=$(find $PROJECT_FOLDER -name "build.xml")
BUILD_T=$(find $TEST_FOLDER -name "build.xml")
i_echo "$TAG SDK PROJECT"

w_echo "#SDK#"

echo "Package -> $PACKAGE"
echo "ID -> $ID"
echo "proj fold -> $PROJECT_FOLDER"
echo "test fold -> $TEST_FOLDER"
echo "local dir -> $dlocalDir"
echo "device dir -> $deviceDir"
