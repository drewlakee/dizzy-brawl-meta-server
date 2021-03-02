#!/bin/bash

# Script developed for stress testing
#
# Description:
#   Script calculates minimal, maximum and average requests per second
#
# Input:
#   $1 - curl options. For example, specific URL
#   $2 - flag for print steps of calculates

let MAX=0
let MIN=99999999999
let AVERAGE=0
let RPC_RESULT=0
let SUM=0
let MAX_STEPS=10

for ((i=0; i < MAX_STEPS; i++))
do
     RPC_RESULT=$(./rps.sh "$1")
     SUM=$((SUM + RPC_RESULT))

     if [[ $RPC_RESULT -gt $MAX ]]
     then
     	MAX=$RPC_RESULT
     fi

     if [[ $RPC_RESULT -lt $MIN ]]
     then
     	MIN=$RPC_RESULT
     fi

     if  [[ -n "$2" && $2 = "--show-steps" ]]
     then
     	echo "STEP $i: $RPC_RESULT"
     fi
done

AVG=$((SUM / MAX_STEPS))

echo -e "$1 \t MIN: $MIN req/s, MAX: $MAX req/s, AVG: $AVG req/s"
