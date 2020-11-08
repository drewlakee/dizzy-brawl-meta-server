# Dizzy Brawl Database Server

### Content

1. [API Navigation](#api-navigation)
2. [API Documentation](#api-documentation)

### API Navigation

1. [Account](#post-accountregister)
    * [POST /account/auth/login](#post-accountauthlogin)
    * [POST /account/register](#post-accountregister)
2. [Character](#character)
    * [POST /characters/get/all](#post-charactersgetall)
    * [POST /characters/get/mesh/all](#post-charactersgetmeshall)
3. [Task](#task)
    * [POST /tasks/get/all](#post-tasksgetall)
    * [POST /tasks/add](#post-tasksadd)
    * [PUT /tasks/update/progress](#put-tasksupdateprogress)

### API Documentation

- <a href="https://documenter.getpostman.com/view/12029239/TVYKawXj">API in Postman Documentation</a>

- Prefix for all end-points `/api/v1`

## Account

### POST `/account/auth/login`

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

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty
error                   |DOESNT_EXIST_AT_DATABASE                    | User's account doesn't exist at database
error                   |INVALID_PASSWORD                            | Incorrect password enter

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
account_uuid        | UUID          | Generated UUID for registered verifiedAccount

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty
error                   |ALREADY_EXIST_AT_DATABASE                   | User with same account already exist at database

## Character

### POST `/characters/get/all`

**JSON Query**

Name                |Data Type      | Description
------------        | ------------- | -------------
account_uuid        | String        |  Account's UUID

**JSON Response**

Name                | Data Type                    | Description
------------        |-------------                 |-------------
characters          | Array of Characters          | Array of characters that must be returned by account uuid

Character Structure

Name                | Data Type     |Description
------------        |-------------  |-------------
character_uuid      | UUID          | Character's UUID
character_type_id   | int           | Character's type of pawn
is_enabled          | Boolean       | Available 

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |INVALID_UUID                                | UUIDs have wrong format
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty

### POST `/characters/get/mesh/all`

**JSON Query**

Name                |Data Type               | Description
------------        | -------------          | -------------
characters          | Array of Strings       |  Array must contain characters UUIDs

**JSON Response**

Name                | Data Type                                 |Description
------------        |-------------                              |-------------
characters          | Array of Character                        | Contains array of characters

Character Structure

Name                | Data Type         |Description
------------        |-------------      |-------------
character_uuid      | UUID              | Character's in query passed UUID
meshes              | Array of Meshes   | Array that contains all character meshes

Mesh structure

Name                | Data Type     |Description
------------        |-------------  |-------------
character_mesh_id   | int           | Character mesh ID
in_game_cost        | int           | Cost in game money
is_enable           | Boolean       | Available 

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |INVALID_UUID                                | UUIDs have wrong format
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty

## Task

### POST `/tasks/get/all`

**JSON Query**

Name                |   Data type   | Description
------------        | ------------- | -------------
account_uuid        | String        |  Task's owner account UUID

**JSON Response**

If at request moment task spend time after generation MORE than "active_interval" parameter 
task instantly deletes from database

If no active task at user's account, response will be empty JSON Array

Name                | Data Type                 | Description
------------        |-------------              |-------------
tasks               | Array of Tasks            | Active Tasks on User's account

Task Structure

Name                | Data Type     | Description
------------        |-------------  |-------------
task_uuid           | UUID          | Task's UUID
task_type_id        | int           | Task type id
current_state       | int           | Current progress of task
goal_state          | int           | Goal value for task complete
time_spends         | int           | Time spends after generation in **minutes** 
active_interval     | int           | Time of active status interval in **minutes**

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |INVALID_UUID                                | UUIDs have wrong format

### POST `/tasks/add`

**Transactional operation**. If some task will be not added - other tasks also will be not added

**JSON Query**

Name                | Data Type         | Description
------------        | -------------     | -------------
tasks               |  Array Of Tasks   |  Array of Tasks that must be added

Task Structure 

Name                | Data Type     | Description
------------        | ------------- | -------------
account_uuid        |   String      |  Task's owner verifiedAccount UUID
task_type_id        |   int         |  Task's type
current_state       |   int         |  Current progress of task
goal_state          |   int         |  Goal value for task complete
active_interval     |   int         |  Time interval in that task will be active. Time in **minutes**

**JSON Response**

Response has same sequenced as request JSON, so UUIDs in same place
as added tasks before

Name                | Data Type                 | Description
------------        |-------------              |-------------
tasks               | Array of Strings          | Array contains generated tasks UUIDs

Task Response Structure

Name                    | Data Type                 | Description
------------            |-------------              |-------------
task_uuid               | UUID                      | Unique generated task key in database

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |INVALID_UUID                                | UUIDs have wrong format
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty

### PUT `/tasks/update/progress`

**Transactional operation**. If some task will not update from a query - other too will be not update

**JSON Query**

Name                | Data Type             | Description
------------        | -------------         | -------------
tasks               |   Array of Tasks      |  Array of Tasks that must be updated

Task Structure

Name                | Data Type     | Description
------------        | ------------- | -------------
task_uuid           |   String      |  Task's UUID that will be updated
current_state       |   int         |  Current progress to update

**JSON Response**

Name                | Data Type     | Description
------------        |-------------  |-------------
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format

**HTTP Response Codes**

Code                | Description
------------        |-------------
200                 | If all was updated - means OK
419                 | Means that some can't be updated, because DB doesn't store inputted Tasks UUIDs

**JSON Error Response Reasons**

Parameter               |Error Name                                  |Description
|------------           |------------                                |-------------
error                   |EMPTY_BODY                                  | Empty json body request
error                   |INVALID_UUID                                | UUIDs have wrong format
error                   |EMPTY_JSON_PARAMETERS                       | Some parameters is empty

