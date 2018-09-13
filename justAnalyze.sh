#!/bin/bash
source settings.sh

analyzerJar="jars/Analyzer.jar"
trace="-TestOriented"
machine=''
getSO machine
if [ "$machine" == "Mac" ]; then
    SED_COMMAND="gsed" #mac
    MKDIR_COMMAND="gmkdir"
else 
    SED_COMMAND="sed" #linux
    MKDIR_COMMAND="mkdir"   
fi

OLDIFS=$IFS
DIR="$HOME/GDResults/*"

for f in $DIR/
    do
    rm -rf $f/Testresults.csv
    #files=$(find $f -not \( -path $f/oldRuns -prune \) -name "Green*.csv")
    java -jar $analyzerJar $trace $f "-Monkey"  ##RR  ##RR
done