#!/bin/bash
source settings.sh

pathProject=$1
pathTests=$2
projtype=$3
package=$4
resDir=$5

machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
	SED_COMMAND="gsed" #mac
else 
	SED_COMMAND="sed" #linux	
fi

TAG="[APP INSTALLER]"
echo ""

i_echo "$TAG Installing the apps on the device"
#find the apk files
if [ "$projtype" == "SDK" ]; then
	appAPK=($(find $pathProject -name "*-debug.apk" | grep -v $pathTests))
	testAPK=($(find $pathTests -name "*-debug.apk"))
elif [ "$projtype" == "GRADLE" ]; then
	appAPK=($(find $pathProject -name "*-debug.apk"))
	testAPK=($(find $pathProject -name "*debug-androidTest*.apk"))
fi

OK="0"

if [ "${#appAPK[@]}" != 1 ] || [ "${#testAPK[@]}" != 1 ]; then

	if [ "${#appAPK[@]}" > 1 ] && [ "${#testAPK[@]}" == 1 ]; then
		pAux=$(echo "${testAPK[0]}" | $SED_COMMAND -r "s#\/[a-zA-Z0-9-]+-debug.+.apk#/#g")
		appAPK=($(find $pAux -name "*-debug.apk"))
		if [ "${#appAPK[@]}" == 1 ]; then
			OK="1"
		fi

	elif [ "${#appAPK[@]}" == 1 ] && [ "${#testAPK[@]}" > 1 ]; then
		pAux=$(echo "${appAPK[0]}" | $SED_COMMAND -r "s#\/[a-zA-Z0-9-]+-debug.+.apk#/#g")
		ppAux=$(dirname $pAux)
		echo "folder is -> $ppAux"
		bqq=($(find $ppAux -name "*debug-androidTest*.apk"))
		echo "testApk -> $bqq"
		if [ "${#bqq[@]}" -ge "1" ]; then
			OK="1"
		fi
	else
		#Either there's no apk files found for the app and/or tests, 
		#or there are 2 or more apks for both app and tests
		OK="0"
	fi
else
	OK="1"
fi

if [[ "$OK" != "1" ]]; then
	e_echo "$TAG Error: Unexpected number of .apk files found."
	e_echo "$TAG Expected: 1 App .apk, 1 Test .apk |  Finded : ${#appAPK[@]} App .apk's, ${#testAPK[@]} Test .apk's"
	w_echo "[ERROR] Aborting..."
	exit 1
else
	w_echo "$TAG Ready to install generated Apps -> Finded : ${#appAPK[@]} App .apk's, ${#testAPK[@]} Test .apk's"
	w_echo "$TAG installing App .apk's"
	adb install -r ${appAPK[0]}
	w_echo "$TAG installing Test .apk's"
	adb install -r ${testAPK[0]}

fi
exit 0