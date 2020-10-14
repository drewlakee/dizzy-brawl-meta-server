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
    * [GET /characters/{accountuuid}](#get-charactersaccount_uuid)

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
character_type_id   | int           | Character's type of pawn
is_enabled          | Boolean       | Available 
error               | String        | **Optional.** Fact of wrong query execution or incorrect path parameter format





