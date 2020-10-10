# Dizzy Brawl Database Server APIv1

API List:

- [POST {host}/api/v1/auth/login](#post-hostapiv1authlogin)
- [POST {host}/api/v1/account/register](#post-hostapiv1accountregister)

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
