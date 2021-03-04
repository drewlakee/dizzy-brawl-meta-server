#!/bin/bash

# Script developed for stress testing
#
# Description:
#   Script calculates minimal, maximum and average duration of requests in milliseconds
#
# Input:
#   $1 - curl options. For example, specific URL
#   $2 - flag for print steps of calculates

let MAX=0
let MIN=99999999999
let AVERAGE=0
let RD_RESULT=0
let SUM=0
let MAX_STEPS=30

for ((i=0; i < MAX_STEPS; i++))
do
     RD_RESULT=$(./rd.sh "$1")
     SUM=$((SUM + RD_RESULT))

     if [[ $RD_RESULT -gt $MAX ]]
     then
     	MAX=$RD_RESULT
     fi

     if [[ $RD_RESULT -lt $MIN ]]
     then
     	MIN=$RD_RESULT
     fi

     if  [[ -n "$2" && $2 = "--show-steps" ]]
     then
     	echo "STEP $i: $RD_RESULT"
     fi
done

AVG=$((SUM / MAX_STEPS))

echo -e "MIN: $MIN ms/req, MAX: $MAX ms/req, AVG: $AVG ms/req"
