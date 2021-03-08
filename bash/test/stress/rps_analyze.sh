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
    # create tmp file for rps results
    touch rps_intermediate_analyze_result.txt

    # 8 cpu count on my machine
    # parallel calls in one second
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    ./rps.sh "$1" >> rps_intermediate_analyze_result.txt &
    wait

    # sum results from every thread
    while read line; do
      RPC_RESULT=$((RPC_RESULT + line))
    done < rps_intermediate_analyze_result.txt

    # add one rps call with general sum
    SUM=$((SUM + RPC_RESULT))

    # delete after intermediate rps call
    rm rps_intermediate_analyze_result.txt

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

    RPC_RESULT=0
done

AVG=$((SUM / MAX_STEPS))

echo -e "MIN: $MIN req/s, MAX: $MAX req/s, AVG: $AVG req/s"
