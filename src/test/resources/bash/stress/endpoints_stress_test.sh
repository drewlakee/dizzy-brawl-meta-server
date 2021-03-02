#!/bin/bash

echo "-----------Endpoints Stress Tests-----------"
echo
echo -e "curl options\t\t\tResults of test"
echo

./rps_analyze.sh '-X GET https://jsonplaceholder.typicode.com/todos/1'
./rd_analyze.sh '-X GET https://jsonplaceholder.typicode.com/todos/1'

echo

./rps_analyze.sh '-X GET https://jsonplaceholder.typicode.com/posts'
./rd_analyze.sh '-X GET https://jsonplaceholder.typicode.com/posts'
