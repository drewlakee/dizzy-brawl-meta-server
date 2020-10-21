# Dizzy Brawl Database Server

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
    * [POST /task/add](#post-taskadd)
    * [PUT /task/update/progress](#put-taskupdateprogress)

### API Documentation

:exclamation: Prefix for all end-points `/api/v1` :exclamation:

## Auth

### POST `/auth/login`

**JSON Query**

Name                | Data Type     | Description
------------        | ------------- | -------------
username_or_email   |  String       |  User's username or email
password            |  String       |  User's password

**JSON Response**

Name                | Data Type     |Description
------------        |-------------  |-------------
account_uuid        | UUID          | Account's UUID
username            | String        | User's in game username
email               | String        | User's email
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**Error reasons**

Error Name                  |Description
------------                |-------------
EMPTY_BODY                  | Empty json body request
DOESNT_EXIST_AT_DATABASE    | User's account doesn't exist at database
INVALID_PASSWORD            | Incorrect password enter

## Account

### POST `/account/register`

**JSON Query**

Name                |   Data Type   | Description
------------        | ------------- | -------------
username            |  String       |  User's username or email
email               |  String       |  User's email
password            |  String       |  User's password

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

Name                |Data Type      | Description
------------        | ------------- | -------------
account_uuid        | String        |  Account's UUID

**JSON Response**

Response wrapped into JSON Array

Name                | Data Type     |Description
------------        |-------------  |-------------
character_uuid      | UUID          | Character's UUID
account_uuid        | UUID          | Owner's account UUID
character_type_id   | int           | Character's type of pawn
is_enabled          | Boolean       | Available 
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**Error reasons**

Error Name                      |Description
------------                    |-------------
EMPTY_BODY                      | Empty json body request
INVALID_QUERY_PARAMETER_FORMAT  | Incorrect query format

## Task

### GET `/task/all`

**JSON Query**

Name                |   Data type   | Description
------------        | ------------- | -------------
account_uuid        | String        |  Task's owner account UUID

**JSON Response**

Response wrapped into JSON Array

If at request moment task spend time after generation MORE than "active_interval" parameter 
task instantly deletes from database

If no active task at user's account, response will be empty JSON Array

Name                | Data Type     | Description
------------        |-------------  |-------------
task_uuid           | UUID          | Task's UUID
account_uuid        | UUID          | Owner's account UUID
task_type_id        | int           | Task type id
current_state       | int           | Current progress of task
goal_state          | int           | Goal value for task complete
time_spends         | int           | Time spends after generation in **minutes** 
active_interval     | int           | Time of active status interval in **minutes**
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**Error reasons**

Error Name                      |Description
------------                    |-------------
EMPTY_BODY                      | Empty json body request
INVALID_QUERY_PARAMETER_FORMAT  | Incorrect query format

### POST `/task/add`

**JSON Query**

Query must be wrapped into JSON Array (If single task, anyway wrap it to JSON Array)

Name                | Data Type     | Description
------------        | ------------- | -------------
account_uuid        |   String      |  Task's owner account UUID
task_type_id        |   int         |  Task's type
current_state       |   int         |  Current progress of task
goal_state          |   int         |  Goal value for task complete
active_interval     |   int         |  Time interval in that task will be active. Time in **minutes**

**JSON Response**

Response has same sequenced as request JSON, so UUIDs in same place
as added tasks before

Name                | Data Type     | Description
------------        |-------------  |-------------
task_uuid           | UUID          | Generated task's UUID
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**Error reasons**

Error Name                      |Description
------------                    |-------------
EMPTY_BODY                      | Empty json body request
INVALID_QUERY_PARAMETER_FORMAT  | Incorrect query format

### PUT `/task/update/progress`

**JSON Query**

Query must be wrapped into JSON Array (If single task, anyway wrap it to JSON Array)

Name                | Data Type     | Description
------------        | ------------- | -------------
task_uuid           |   String      |  Task's UUID that will be updated
current_state       |   int         |  Current progress to update
goal_state          |   int         |  Goal value to update

**JSON Response**

Name                | Data Type     | Description
------------        |-------------  |-------------
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**HTTP Response Codes**

Code                | Description
------------        |-------------
200                 | If all was updated - means OK
419                 | Means that some was not updated, because DB doesn't store inputted Task UUID

**Error reasons**

Error Name                      |Description
------------                    |-------------
EMPTY_BODY                      | Empty json body request
INVALID_QUERY_PARAMETER_FORMAT  | Incorrect query format
