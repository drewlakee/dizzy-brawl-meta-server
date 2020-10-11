# Dizzy Brawl Database Server APIv1

API List:

- [POST {host}/api/v1/auth/login](#post-hostapiv1authlogin)
- [POST {host}/api/v1/account/register](#post-hostapiv1accountregister)
- [GET {host}/api/v1/characters/:accountuuid](#get-hostapiv1charactersaccountuuid)

###### POST {host}/api/v1/auth/login

Request params:

- usernameoremail: username or email
- password: user password

Response:

- user_account_info
- valid: correct username/email and password enter
- found: database have or doesn't have information about user

Response examples:

- Correct enter

```json
{
    "account_uuid": "cd318932-745f-4c4d-9462-c1a42d455860",
    "username": "User1",
    "email": "user1@gmail.com",
    "found": true,
    "valid": true
}
```

- Was founded, but incorrect password

```json
{
    "found": true,
    "valid": false
}
```

###### POST {host}/api/v1/account/register

Request params:

- username
- email
- password

Response:

- user_account_info
- success: result of registration

Response examples:

- Successful registration

```json
{
    "account_uuid": "04759d99-913a-4982-9860-6b7a29f7333d",
    "username": "testReg4",
    "email": "testReg4@gmail.com",
    "success": true
}
```

- Username or email already registered

```json
{
    "success": false
}
```

###### GET {host}/api/v1/characters/:accountuuid

Request params:

- accountuuid: uuid of user's account

Response:

- character_info

Response examples:

- Successful fetch

```json
[
    {
        "character_uuid": "bbe94cf4-1beb-46ca-8604-c62ccdebedf0",
        "character_type_id": 3,
        "is_enabled": false
    },
    {
        "character_uuid": "84a77243-2c88-4d10-a9d7-a6bcd37a140c",
        "character_type_id": 2,
        "is_enabled": true
    },
    {
        "character_uuid": "9870d39f-bd41-4315-96e1-680f07a00209",
        "character_type_id": 1,
        "is_enabled": true
    }
]
```

- Wrong parameter

```json
{
    "message": "wrong account uuid format"
}
```





