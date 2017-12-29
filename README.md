# maggie-login
A brief description for Maggie login module (in Java)

## Maggie Project Introduction
Maggie is a social platform with attributes of strong authentication, high privacy, weak relationship and strong personality. 
<br/><br/>
Maggie enables users to accomplish value transfer more efficiently by data analysis and matching with intelligent contract, and builds a social ecosphere based on block chain with value transform, which aims at changing the traditional centralized social model with block chain technology.
<br/><br/>
Maggie establishes a trust system spontaneously by taking full advantage of immutable block chain network.

## Login Architecture
Application server of Maggie has a built-in official CA root certificate, which can be verified directly by mobile devices. That helps us establish secure HTTPS/WSS connection, so that all the interactive contents between server and APP are protected completely. 
<br/><br/>
Instead of using traditional username & password, Maggie selects key-authentication method to login. That means, server verifies user certification and send random challenge message every time when connection is established, then APP use its private key to decrypt, encrypt again and send it back to server for authentication.
<br/><br/>
<div align="center">
  <img src="https://github.com/WuShengRan/maggie-login/blob/master/pictures/architecture.png" width = "621" height = "595" alt="Login_Arch" /></div>

## Login Process
1.	Open APP, send request for SMS verification code
2.	Server checks whether the phone number is registered. If already registered, use certification issued by CA center to login. If not, enter the register process.
<br/><br/>
![Login_Proc](https://github.com/WuShengRan/maggie-login/blob/master/pictures/process.png)
### Register process:
1.	Download server certification first to establish https connection
2.	Generate private and public key locally
3.	Set nickname and personal information
4.	Send request with public key and personal info for cert
5.	Login with cert
### Cert-login process:
1.	Send cert to server for authentication
2.	Server sends challenge message encrypted with user’s public key
3.	Receive challenge message, decrypt it and encrypt it again with private key, then send back to server
4.	Server verifies challenge message, login sucessfully

## Build the project & test the login API with curl command
This is a spring-boot project with gradle as its build tool, so it can easily be built by gradle or maven, and will start an tomcat server with port 8443 open to establish **HTTPS** connections.
<br/><br/>
The login APIs are all encapsulated as **POST** request, so that we can use commands such as *curl* or *wget* to test the module.
<br/><br/>
For example, to get register code:
<br/><br/>
`curl -c cookies –d 'data={"mobile":"`*`PHONE_NUMBER`*`"}' https://localhost:8443/user/registCode`
<br/><br/>
To send register code to server for verification:
<br/><br/>
`curl -b cookies -d 'data={"mobile":"`*`PHONE_NUMBER`*`","registCode":"`*`RANDOM_CODE`*`"}' https://localhost:8443/user/validMobile`
<br/><br/>
To send public key and user info to server for applying certification:
<br/><br/>
`curl -b cookies -d 'data= {"equipCode":"`*`EQUIPMENT_NO`*`", "pubKey":"`*`PUBLIC_KEY`*`", "userType":"`*`USER_TYPE`*`", "nickName":"`*`NICKNAME`*`","gender":"`*`GENDER`*`","birthday":"`*`BIRTHDAY`*`","city":"`*`CITY`*`"}' https://localhost:8443/user/cert`
<br/><br/>
To send cert to server and get challenge message for login:
<br/><br/>
`curl -b cookies -d 'data={"cert":"`*`CERT`*`"}' https://localhost:8443/user/toLogin`
<br/><br/>
To send encrypted challenge message back to server for login verification:
<br/><br/>
`curl -b cookies -d 'data={"poKey":"`*`ENCRYPT_CHALLENGE_MESSAGE`*`"}' https://localhost:8443/user/login`
