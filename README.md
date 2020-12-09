# Dizzy Brawl Meta-Server

### Content

1. [APIv1 Navigation](#apiv1-navigation)
2. [APIv1 Documentation](#apiv1-documentation)

### APIv1 Navigation

1. [Account](#account)
    * [POST /account/auth/login](#post-accountauthlogin)
    * [POST /account/register](#post-accountregister)
2. [Character](#character)
    * [POST /characters/get/all](#post-charactersgetall)
    * [POST /characters/armors/get/all](#post-charactersarmorsgetall)
3. [Task](#task)
    * [POST /tasks/get/all](#post-tasksgetall)
    * [POST /tasks/add](#post-tasksadd)
    * [PUT /tasks/update/progress](#put-tasksupdateprogress)
4. [Server](#server)
    * [POST /servers/get/all](#post-serversgetall)
    * [POST /servers/add](#post-serversadd)
    * [DELETE /servers/delete](#delete-serversdelete)

### APIv1 Documentation

- <a href="https://documenter.getpostman.com/view/12029239/TVYKawXj">API in Postman Documentation</a>

## Account

### POST `/account/auth/login`

**JSON Query**

name                | data type     | description
------------        | ------------- | -------------
username_or_email   |  string       |  user's username or email
password            |  string       |  user's password

**JSON Response**

name                | data type     |description
------------        |-------------  |-------------
account_uuid        | uuid          | account's uuid
username            | string        | user's in game username
email               | string        | user's email

[To API Navigation](#apiv1-navigation)

### POST `/account/register`

**JSON Query**

name                |   data type   | description
------------        | ------------- | -------------
username            |  string       |  user's username or email
email               |  string       |  user's email
password            |  string       |  user's password

**JSON Response**

name                | data type     |description
------------        |-------------  |-------------
account_uuid        | uuid          | generated uuid for registered account

[To API Navigation](#apiv1-navigation)

## Character

### POST `/characters/get/all`

**JSON Query**

name                |data type      | description
------------        | ------------- | -------------
account_uuid        | string        |  account's uuid

**JSON Response**

name                | data type                    | description
------------        |-------------                 |-------------
characters          | array of characters          | array of characters that must be returned by account uuid

single "character" json object

name                | data type     |description
------------        |-------------  |-------------
character_uuid      | uuid          | character's uuid
character_type_id   | int           | character's type of pawn
is_enabled          | Boolean       | available for account

[To API Navigation](#apiv1-navigation)

### POST `/characters/armors/get/all`

**JSON Query**

name                |data type               | description
------------        | -------------          | -------------
account_uuid         | string       |  armors owner account uuid

**JSON Response**

name                | data type                                 |description
------------        |-------------                              |-------------
armors          | array of armors                        | contains array of armors data

single "armor" json object

name                | data type         |description
------------        |-------------      |-------------
armor_id            | uuid              | character's in query passed uuid
armor_name             | string   |     name    
armor_type          |  string  |        type name
cost           |  int  |                in game cost
armor_level                |  int  |    in game level
is_enabled             |  Boolean  |    is available for account

[To API Navigation](#apiv1-navigation)

## Task

### POST `/tasks/get/all`

**JSON Query**

name                |   data type   | description
------------        | ------------- | -------------
account_uuid        | string        |  task's owner account uuid

**JSON Response**

if at request moment task spend time after generation MORE than "active_interval" parameter 
task instantly deletes from database

if no active task at user's account, response will be empty JSON Array

name                | data type                 | description
------------        |-------------              |-------------
tasks               | array of tasks            | active Tasks on user's account

single "task" json object

name                | data type     | description
------------        |-------------  |-------------
task_uuid           | uuid          | task's uuid
task_type_id        | int           | task type id
current_state       | int           | current progress of task
goal_state          | int           | goal value for task complete
active_interval     | int           | time of active status interval in **minutes**

[To API Navigation](#apiv1-navigation)

### POST `/tasks/add`

**JSON Query**

name                | data type         | description
------------        | -------------     | -------------
tasks               |  array of tasks   |  array of tasks that must be added

single "task" json object

name                | data type     | description
------------        | ------------- | -------------
account_uuid        |   string      |  task's owner verifiedAccount uuid
task_type_id        |   int         |  task's type
current_state       |   int         |  current progress of task
goal_state          |   int         |  goal value for task complete
active_interval     |   int         |  time interval in that task will be active. Time in **minutes**

**JSON Response**

response has same sequenced as request JSON, so uuids in same place
as added tasks before

name                | data type                 | description
------------        |-------------              |-------------
tasks               | array of strings          | array contains generated tasks uuids

single json object

name                    | data type                 | description
------------            |-------------              |-------------
task_uuid               | uuid                      | unique generated task key in database

[To API Navigation](#apiv1-navigation)

### PUT `/tasks/update/progress`

**JSON Query**

name                | data type             | description
------------        | -------------         | -------------
tasks               |   array of tasks      |  array of tasks that must be updated

single "task" json object

name                | data type     | description
------------        | ------------- | -------------
task_uuid           |   string      |  Task's uuid that will be updated
current_state       |   int         |  Current progress to update

**HTTP Response Codes**

code                | description
------------        |-------------
200                 | if all was updated - means ok
419                 | means that some can't be updated, because DB doesn't store inputted Tasks uuids

[To API Navigation](#apiv1-navigation)

## Server

### POST `/servers/get/all`

**JSON Response**

name                | data type     | description
------------        |-------------  |-------------
server_uuid               | string        | unique server identifier 
ip_v4                | string        |  server's ipv4
game_mode_id                | int        | game mode id
game_mode_name      | string | game mode name in game

[To API Navigation](#apiv1-navigation)

### POST `/servers/add`

**JSON Query**

name                | data type             | description
------------        | -------------         | -------------
servers               |   array of servers      |  array of servers that must be added

single "server" json object

name                | data type             | description
------------        | -------------         | -------------
ip_v4               |   string     |  ipv4 of server
game_mode_id               |   int      |  game mode of server

**JSON Response**

name                | data type     | description
------------        |-------------  |-------------
servers               | array of strings        | unique servers identifier in database

single json object

name                | data type     | description
------------        |-------------  |-------------
server_uuid               | string       | unique server uuid in database

[To API Navigation](#apiv1-navigation)

### DELETE `/servers/delete`

**JSON Query**

name                | data type             | description
------------        | -------------         | -------------
servers               |   array of strings      |  array of unique identifiers of servers in database

single json object

name                | data type             | description
------------        | -------------         | -------------
server_uuid               |   string      |  unique identifier of server in database

**HTTP Response Codes**

code                | description
------------        |-------------
200                 | ok - all deleted
404                 | some server not founded in database and other servers in query also was not deleted

[To API Navigation](#apiv1-navigation)