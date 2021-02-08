# Dizzy Brawl Game Meta-Web-Server + PostgreSQL + Prometheus + Grafana

![dizzy_head](https://sun9-28.userapi.com/impf/hA6E0YonDbEE3gviC95XEQpY5yuLNWmzCDZqxQ/u8LzTYPXQ0E.jpg?size=795x200&quality=95&crop=0,87,1647,414&sign=9a4a0fc889f3ed9f899efecbbd0d4ef4&type=cover_group)

### Content

1. [APIv1 Navigation](#apiv1-navigation)
2. [APIv1 Documentation](#apiv1-documentation)
3. [Deployment](#deployment)

### APIv1 Navigation

1. [Account](#account)
    * [POST /accounts/auth/login](#post-accountsauthlogin)
    * [POST /accounts/register](#post-accountsregister)
2. [Character](#character)
    * [POST /characters/get/all](#post-charactersgetall)
    * [POST /characters/armors/get/all](#post-charactersarmorsgetall)
    * [POST /characters/weapons/get/all](#post-charactersweaponsgetall)
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

### POST `/accounts/auth/login`

Authenticate user and get his credentials.

**JSON Body**

name                | data type     | description
------------        | ------------- | -------------
username_or_email   |  string       |  username or email
password            |  string       |  user password

**JSON Body Response**

name                | data type     |description
------------        |-------------  |-------------
account_id        | long          | unique identifier
username            | string        | in game username
email               | string        | user email

[To API Navigation](#apiv1-navigation)

### POST `/accounts/register`

Register, authenticate user and get his credentials back.

**JSON Body**

name                |   data type   | description
------------        | ------------- | -------------
username            |  string       |  user's username or email
email               |  string       |  user's email
password            |  string       |  user's password

**JSON Body Response**

name                | data type     |description
------------        |-------------  |-------------
account_id        | long          | unique identifier

[To API Navigation](#apiv1-navigation)

## Character

### POST `/characters/get/all`

Get all characters on user's account.

**JSON Body Query**

name                |data type      | description
------------        | ------------- | -------------
account_id        | long        |  unique identifier

**JSON Body Response**

name                | data type                    | description
------------        |-------------                 |-------------
characters          | json array of characters     | user's characters array

single "character" json object

name                | data type     |description
------------        |-------------  |-------------
character_id        | long          | unique identifier
character_type_id   | int           | unique identifier of character's type in game
character_name      | string  | character's type name
is_enabled          | boolean       | available for user in game or not

[To API Navigation](#apiv1-navigation)

### POST `/characters/armors/get/all`

Get all armors for concrete characters of user on user's account.

**JSON Body Query**

name                |data type               | description
------------        | -------------          | -------------
characters         | json array       |  characters whose armors you want to request

single json object at array

name                |data type               | description
------------        | -------------          | -------------
character_id         | long       |  concrete character unique identifier to request

**JSON Body Response**

name                | data type                                 |description
------------        |-------------                              |-------------
armors          | json array of armors                        | character's armors

single "armor" json object

name                | data type         |description
------------        |-------------      |-------------
armor_id            | int              | unique identifier
armor_name             | string   |     armor game name
armor_cost           |  int  |                in game cost
armor_level                |  int  |    in game level
is_enabled             |  boolean  |    available for user's character in game or not

[To API Navigation](#apiv1-navigation)

### POST `/characters/weapons/get/all`

Get all weapons for concrete characters of user on user's account.

**JSON Body Query**

name                |data type               | description
------------        | -------------          | -------------
characters         | json array       |  characters whose weapons you want to request

single json object at array

name                |data type               | description
------------        | -------------          | -------------
character_id         | long       |  concrete character unique identifier to request

**JSON Body Response**

name                | data type                                 |description
------------        |-------------                              |-------------
weapons          | json array of weapons                        | character's weapons

single "weapon" json object

name                | data type         |description
------------        |-------------      |-------------
weapon_id            | int              | unique identifier
weapon_name             | string   |     weapon game name
weapon_cost           |  int  |                in game cost
weapon_level                |  int  |    in game level
is_enabled             |  boolean  |    available for user's character in game or not

[To API Navigation](#apiv1-navigation)

## Task

### POST `/tasks/get/all`

Get all current tasks on account.

**JSON Body Query**

name                |   data type   | description
------------        | ------------- | -------------
account_id        | long        |  unique identifier to request

**JSON Body Response**

if at request moment task spend time after generation MORE than "active_interval" parameter 
task instantly deletes from database

if no active task at user's account, response will be empty JSON Array

name                | data type                 | description
------------        |-------------              |-------------
tasks               | json array of tasks            | active Tasks on user's account

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

Add tasks to account.

**JSON Body Query**

name                | data type         | description
------------        | -------------     | -------------
tasks               |  json array of tasks   |  array of tasks that must be added

single "task" json object

name                | data type     | description
------------        | ------------- | -------------
account_id        |   long      |  unique identifier of account to add
task_type_id        |   int         |  task's type
current_state       |   int         |  current progress of task
goal_state          |   int         |  goal value for task complete
active_interval     |   int         |  time interval in that task will be active. Time in **minutes**

**JSON Body Response**

response has same sequenced as request JSON, so uuids in same place
as added tasks before

name                | data type                 | description
------------        |-------------              |-------------
tasks               | json array of strings          | array contains generated tasks uuids

single json object

name                    | data type                 | description
------------            |-------------              |-------------
task_uuid               | uuid                      | unique identifier

[To API Navigation](#apiv1-navigation)

### PUT `/tasks/update/progress`

Update task progress of current state.

**JSON Body Query**

name                | data type             | description
------------        | -------------         | -------------
tasks               |   json array of tasks      |  array of tasks that must be updated

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

### Deployment

1. Clone project from repository

```
git clone git@github.com:drewlakee/dizzy-brawl-meta-server.git
```

2. Run docker-composer

```
docker-compose up
```

Available applications by default:

- PostgreSQL: available on [localhost:5433](http://localhost:5433/)

   - database:    dizzy-brawl

   - admin:       dizzy-brawl
   
   - password:    dizzy-brawl

- Web-server: available on [localhost:8080](http://localhost:8080/)

   - endpoints-prefix: /api/v1

- Prometheus: available on [localhost:9090](http://localhost:9090/)

   - scrape-jobs:
      - prometheus - every 15s
      - web-server - every 15s

- Grafana: available on [localhost:3000](http://localhost:3000/)

   - username:    admin
   
   - password:    admin
