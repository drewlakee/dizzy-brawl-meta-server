#!/bin/bash

# Script developed for stress testing
#
# Description:
#   Making curl request on specific endpoint and multiplexing {total_time} seconds of it
#   then make milliseconds by command awk with float multiplication
#   and print
#
# Input:
#   $1 - curl options. For example, specific URL

CURL_RESULT=$(curl -w %{time_total} -s -o /dev/null $1)
TIME_IN_SECONDS=$(echo "${CURL_RESULT/[,]/.}"   )
echo $(awk "BEGIN {print int ($TIME_IN_SECONDS * 1000) }")
