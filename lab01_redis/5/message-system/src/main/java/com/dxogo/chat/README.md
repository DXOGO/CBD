# How to run **MessageSystem**.java

## Menu:

```
1) Create user                -> Add new user to database
2) Login/Change user          -> Login to existing user or change current user
3) Follow user                -> Follow existing user
4) Unfollow user              -> Unfollow existing user
5) Send message               -> Send message, whoever follows the user can see it
6) Read received messages     -> Read messages from a specific user you follow
7) Exit                       -> Exit the program
8) Clear DB                   -> Clear the database
```

### TERMINAL COMMANDS:
```
$ mvn package
$ mvn exec:java -Dexec.mainClass="com.dxogo.chat.MessageSystem"
```