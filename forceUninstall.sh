#!/bin/bash
source settings.sh
source util.sh

instTests=($(adb shell pm list instrumentation | cut -f2 -d: | cut -f1 -d\ | cut -f1 -d/))
for i in ${instTests[@]}; do
	a=${i/%.test/}
	adb shell pm uninstall $a
	adb shell pm uninstall $i
	
	a=${i/%.tests/}
	adb shell pm uninstall $a
done
