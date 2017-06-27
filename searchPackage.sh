#!/bin/bash
source settings.sh

DIR=$1
PCK="-"
TPCK="-"

BUILDS=($(find $DIR -name "build.gradle" | grep -v "/build/"))
for x in ${BUILDS[@]}; do
	RES=$(grep "testApplicationId" $x)
	if [ -n "$RES" ] && [ "$TPCK" == "-" ]; then
		delims=\'\"
		IFS="$delims" read -ra arr <<< "$RES"
		TPCK=${arr[1]}
	fi
	RES=$(grep "applicationId" $x)
	if [ -n "$RES" ] && [ "$PCK" == "-" ]; then
		delims=\'\"
		IFS="$delims" read -ra arr <<< "$RES"
		PCK=${arr[1]}
	fi
done

if [ "$TPCK" == "-" ]; then
	TPCK="$PCK.test"
fi
echo "$PCK"
echo "$TPCK"