#!/bin/bash

# Script developed for stress testing
#
# Description:
#   For 1 second script makes curl on specific URL and count handled requests
#
# Input:
#   $1 - curl options. For example, specific URL

let STOP_TIME=$(date +%s)
let STOP_TIME=$((STOP_TIME + 1))
let CURR_TIME=$(date +%s)
let REQUESTS_COUNT=0

while [ $CURR_TIME != $STOP_TIME ]
do
  curl -s -o /dev/null --data $1
  CURR_TIME=$(date +%s)
  REQUESTS_COUNT=$((REQUESTS_COUNT + 1))
done
echo "$REQUESTS_COUNT"
