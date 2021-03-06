#!/bin/bash

RPS_LABEL="Requests Per Second: "
RD_LABEL="Requests Duration: "

HOST="localhost"
PORT="8080"
API_VERSION="v1"

REGISTRATION_OPTIONS="@registration.json $HOST:$PORT/api/$API_VERSION/accounts/register"
echo "Testing... $REGISTRATION_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "'$REGISTRATION_OPTIONS'")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "'$REGISTRATION_OPTIONS'")"

echo

LOGIN_OPTIONS="@login.json $HOST:$PORT/api/$API_VERSION/accounts/auth/login"
echo "Testing... $LOGIN_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "'$LOGIN_OPTIONS'")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "'$LOGIN_OPTIONS'")"
