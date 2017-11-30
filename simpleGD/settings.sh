#!/bin/bash

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

function w_echo {
	echo -e "$yellow_$1$rc_"
}

function i_echo {
	echo -e "$greenB_$1$rc_"
}

function e_echo {
	echo -e "$red_$1$rc_"
}