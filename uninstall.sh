#!/bin/bash
source settings.sh

PACKAGE=$1
TESTPACKAGE=$2

TAG="[APP REMOVER]"

i_echo "$TAG Uninstalling previously installed apps"

#Uninstall the app
echo -n "$TAG Removing App: "
adb shell pm uninstall $PACKAGE

#Uninstall the tests
echo -n "$TAG Removing Tests: "
adb shell pm uninstall $TESTPACKAGE
exit

## list apps
# adb shell pm list packages [-f]