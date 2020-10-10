# Dizzy Brawl Database Server APIv1

API List:

- [POST {host}/api/v1/auth/login](#post-hostapiv1authlogin)

###### POST {host}/api/v1/auth/login

Request params:

- usernameoremail: username or email
- password: user password

Response:

- {user_account_info}: information about user (like email, username e.t.c)
- valid: correct username/email and password enter
- found: database have or doesn't have information about user

Response example:

```json
{
    "account_uuid": "cd318932-745f-4c4d-9462-c1a42d455860",
    "username": "User1",
    "email": "user1@gmail.com",
    "found": true,
    "valid": true
}
```
