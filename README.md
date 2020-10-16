# Dizzy Brawl Database Server

### TODO

- Add "reason" to response where can be error at execution
- HTTP Methods - define correct methods for all apis

### Content

1. [API Navigation](#api-navigation)
2. [API Documentation](#api-documentation)

### API Navigation

1. [Auth](#auth)
    * [POST /auth/login](#post-authlogin)
2. [Account](#account)
    * [POST /account/register](#post-accountregister)
3. [Character](#character)
    * [GET /character/all](#get-characterall)
4. [Task](#task)
    * [GET /task/all](#get-taskall)

### API Documentation

:exclamation: Prefix for all end-points `/api/v1` :exclamation:

## Auth

### POST `/auth/login`

**JSON Query**

Name                | Description
------------        | -------------
username_or_email   |  User's username or email
password            |  User's password

**JSON Response**

Name                | Data Type     |Description
------------        |-------------  |-------------
account_uuid        | UUID          | Account's UUID
username            | String        | User's in game username
email               | String        | User's email
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

Error Name                  |Description
------------                |-------------
EMPTY_BODY                  | Empty json body request
DOESNT_EXIST_AT_DATABASE    | User's account doesn't exist at database
INVALID_PASSWORD            | Incorrect password enter

## Account

### POST `/account/register`

**JSON Query**

Name                | Description
------------        | -------------
username            |  User's username or email
email               |  User's email
password            |  User's password

**JSON Response**

Name                | Data Type     |Description
------------        |-------------  |-------------
account_uuid        | UUID          | Generated UUID for registered account
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

Error Name                      |Description
------------                    |-------------
EMPTY_BODY                      | Empty json body request
INVALID_QUERY_PARAMETER_FORMAT  | Incorrect query format
ALREADY_EXIST_AT_DATABASE       | Account already exist at database

## Character

### GET `/character/all`

**JSON Query**

Name                | Description
------------        | -------------
account_uuid        |  Account's UUID

**JSON Response**

Response wrapped into JSON Array

Name                | Data Type     |Description
------------        |-------------  |-------------
character_uuid      | UUID          | Character's UUID
account_uuid        | UUID          | Owner's account UUID
character_type_id   | int           | Character's type of pawn
is_enabled          | Boolean       | Available 
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

## Task

### GET `/task/all`

**JSON Query**

Name                | Description
------------        | -------------
account_uuid        |  Task's owner account UUID
interval            |  Time interval since generation in **minutes**

**JSON Response**

Response wrapped into JSON Array

If at request moment task spend time after generation MORE than interval parameter 
task instantly deletes from database

Name                | Data Type     | Description
------------        |-------------  |-------------
task_uuid           | UUID          | Task's UUID
account_uuid        | UUID          | Owner's account UUID
task_type_id        | int           | Task type id
current_state       | int           | Current value of task
goal_state          | int           | Goal value for task complete
time_spends         | int           | Time spends after generation in **minutes** 
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format



