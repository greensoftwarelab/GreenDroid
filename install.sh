#!/bin/bash
pathProject=$1
pathTests=$2
projtype=$3
package=$4
resDir=$5

TAG="[APP INSTALLER]"

echo "$TAG Installing the apps on the device"
#find the apk files
if [ "$projtype" == "SDK" ]; then
	appAPK=($(find $pathProject -name "*-debug.apk" | grep -v $pathTests))
	testAPK=($(find $pathTests -name "*-debug.apk"))
elif [ "$projtype" == "GRADLE" ]; then
	appAPK=($(find $pathProject -name "*-debug.apk"))
	pAux=$(echo $appAPK | sed -r "s#\/[a-zA-Z0-9-]+-debug.apk#/#g")
	testAPK=($(find $pAux -name "*-debug-androidTest-*.apk"))
fi

if [ "${#appAPK[@]}" != 1 ] || [ "${#testAPK[@]}" != 1 ]; then
	echo "$TAG Error: Unexpected number of .apk files found."
	echo "$TAG Expected: 1 App .apk, 1 Test .apk |  Found: ${#appAPK[@]} App .apk's, ${#testAPK[@]} Test .apk's"
	echo "[ERROR] Aborting..."
	exit 1
else
	adb install -r ${appAPK[0]}
	adb install -r ${testAPK[0]}

	echo "$TAG Creating support folder..."
	mkdir -p $resDir/$package/all
	#cp $pathProject/_aux_/AllMethods $resDir/$package/all
	cp ./allMethods.txt $resDir/$package/all
fi
exit 0