#!/bin/bash


function w_echo {
	echo -e "$yellow_$1$rc_"
}

function i_echo {
	echo -e "$greenB_$1$rc_"
}

function e_echo {
	echo -e "$red_$1$rc_"
}
function b_echo {
	echo -e "$blue_$1$rc_"
}

function getSO(){
	unameOut="$(uname -s)"
	case "${unameOut}" in
		Linux*)     machine=Linux;;
		Darwin*)    machine=Mac;;
		CYGWIN*)    machine=Cygwin;;
		MINGW*)     machine=MinGw;;
		*)          machine="UNKNOWN:${unameOut}"
	esac
	eval "$1='$machine'"
}

unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${unameOut}"
esac

if [ "$machine" == "Mac" ]; then
	#reset
	rc_="\033[m"
	#normal colors
	red_="\033[31m"
	green_="\033[32m"
	blue_="\033[36m"
	yellow_="\033[33;1m" # \[\033[33;1m\]
	#bold colors
	redB_="\033[31m\033[1m"
	greenB_="\033[32m\033[1m"
	yellowB_="\033[33m\033[1m"
	#b_echo "OS of Host System : MAC OS"

elif [ "$machine" == "Linux" ]; then
	#echo "LINUX SYSTEM DETECTED"
	#reset
	rc_="\e[0m"
	#normal colors
	red_="\e[31m"
	green_="\e[32m"
	yellow_="\e[33m"
	#bold colors
	redB_="\e[31m\e[1m"
	greenB_="\e[32m\e[1m"
	yellowB_="\e[33m\e[1m"
	yellowB_="\e[33m\e[1m"
else
	#echo "UNKNOWN SYSTEM DETECTED"
	#reset
	rc_="\e[0m"
	#normal colors
	red_="\e[31m"
	green_="\e[32m"
	yellow_="\e[33m"
	#bold colors
	redB_="\e[31m\e[1m"
	greenB_="\e[32m\e[1m"
	yellowB_="\e[33m\e[1m"
fi
