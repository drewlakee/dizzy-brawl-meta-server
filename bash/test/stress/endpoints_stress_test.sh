#!/bin/bash

RPS_LABEL="Requests Per Second: "
RD_LABEL="Requests Duration: "

HOST="localhost"
PORT="8080"
API_VERSION="v1"

echo "Test starts at $(date)"
START_TIME=$(date +%s)

echo

#---------------------------------START------------------------------------------------

REGISTRATION_OPTIONS="--data @registration.json $HOST:$PORT/api/$API_VERSION/accounts/register"
echo "Testing... $REGISTRATION_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$REGISTRATION_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$REGISTRATION_OPTIONS")"

echo

LOGIN_OPTIONS="--data @login.json $HOST:$PORT/api/$API_VERSION/accounts/auth/login"
echo "Testing... $LOGIN_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$LOGIN_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$LOGIN_OPTIONS")"

echo

GET_ARMORS_OPTIONS="--data @get_armors.json $HOST:$PORT/api/$API_VERSION/characters/armors/get/all"
echo "Testing... $GET_ARMORS_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$GET_ARMORS_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$GET_ARMORS_OPTIONS")"

echo

GET_TASKS_OPTIONS="--data @get_tasks.json $HOST:$PORT/api/$API_VERSION/tasks/get/all"
echo "Testing... $GET_TASKS_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$GET_TASKS_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$GET_TASKS_OPTIONS")"

echo

GET_SERVERS_OPTIONS="-X POST $HOST:$PORT/api/$API_VERSION/servers/get/all"
echo "Testing... $GET_SERVERS_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$GET_SERVERS_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$GET_SERVERS_OPTIONS")"

echo

GET_CHARACTERS_OPTIONS="--data @get_characters.json $HOST:$PORT/api/$API_VERSION/characters/get/all"
echo "Testing... $GET_CHARACTERS_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$GET_CHARACTERS_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$GET_CHARACTERS_OPTIONS")"

echo

GET_CHARACTERS_WEAPONS_OPTIONS="--data @get_characters_weapons.json $HOST:$PORT/api/$API_VERSION/characters/weapons/get/all"
echo "Testing... $GET_CHARACTERS_WEAPONS_OPTIONS"
echo -e "$RPS_LABEL\t$(./rps_analyze.sh "$GET_CHARACTERS_WEAPONS_OPTIONS")"
echo -e "$RD_LABEL\t$(./rd_analyze.sh "$GET_CHARACTERS_WEAPONS_OPTIONS")"

#---------------------------------STOP------------------------------------------------

echo

STOP_TIME=$(date +%s)
DIFF_SEC=$((STOP_TIME - START_TIME))
echo "Test spend $DIFF_SEC seconds"
