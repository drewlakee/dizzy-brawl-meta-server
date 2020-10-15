# Dizzy Brawl Database Server

### Content

1. [API Navigation](#api-navigation)
2. [API Documentation](#api-documentation)

### API Navigation

1. [Auth](#auth)
    * [POST /auth/login](#post-authlogin)
2. [Account](#account)
    * [POST /account/register](#post-accountregister)
3. [Characters](#characters)
    * [GET /characters/{account_uuid}](#get-charactersaccount_uuid)
4. [Tasks](#task)
    * [GET /tasks/{account_uuid}](#get-tasksaccount_uuid)

### API Documentation

:exclamation: Prefix for all end-points `/api/v1` :exclamation:

## Auth

### POST `/auth/login`

**Query parameters**

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
found               | Boolean       | Database have or doesn't have information about user
valid               | Boolean       | Correct username/email and password enter

## Account

### POST `/account/register`

**Query parameters**

Name                | Description
------------        | -------------
username            |  User's username or email
email               |  User's email
password            |  User's password

**JSON Response**

Name                | Data Type     |Description
------------        |-------------  |-------------
account_uuid        | UUID          | Account's UUID
username            | String        | User's in game username
email               | String        | User's email
success             | Boolean       | Operation's result

## Characters

### GET `/characters/{account_uuid}`

**Path parameters**

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

## Tasks

### GET `/tasks/{account_uuid}`

**Path parameters**

Name                | Description
------------        | -------------
account_uuid        |  Task's owner account UUID

**Query parameters**

Name                | Description
------------        | -------------
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
generated_date      | Timestamp     | Generation date time
time_spends         | int           | Time spends after generation in **minutes** 
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format



