package com.dxogo.chat;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class MessageSystem {

    private Jedis jedis;
    public static String USER = "USER:info:";

    public MessageSystem() {
        this.jedis = new Jedis();
    }

    public Set<String> getUsers() { // get users
        return jedis.keys("*");
    }

    public int LoginUser(String username) {
        if (getUsers().contains(username)){
            System.out.println("Login sucessfull!\n");
            return 1;
        } else {
            System.out.println("User doesn't exists, create an account first!\n");
            return 0;
        }
    }

    public int saveUser(String username) {    // create users
        // if username doesnt exist -> create
        if (!getUsers().contains(username)){
            jedis.rpush(username, "");
            System.out.println("User created sucessfully!\n");
            return 1;
        } else {
                System.out.println("User already exists, try another name!\n");
                return 0;
        }
    }  

    public void followUser(String user1, String user2) {    // follow existing user
        if (!usersFollowing(user2).contains(user1)) {
            jedis.rpush(user2 + USER, user1);
            System.out.println("User followed sucessfully!\n");
        } else {
            System.out.println("User is already following " + user2 + " or user doesn't exist, try another name!\n");
        }
    }

    public void unfollowUser(String user1, String user2){    // unfollow existing user
        if (usersFollowing(user2).contains(user1)) {
            jedis.lrem(user1 + USER, 1, user1);
            System.out.println("User unfollowed sucessfully!\n");
        } else {
            System.out.println("User is not following " + user2 + " or user doesn't exist, try another name!\n");
        }
    }

    public List<String> usersFollowing(String username) {   // currently following
        return jedis.lrange(username + USER, 0, -1);
    }

    public void MyMessagegLog(String username, List<String> message){  // users message log
        if (getUsers().contains(username)){
            String msg = "";
            for (String str : message) {
                msg += str + " " + "\n\n";
            }
            for (String following : usersFollowing(username)) {
                System.out.println("From: " + username + "\nTo: " + following + "\n --> " + msg);
            }
            jedis.rpush(username, msg);

        } else {
                System.out.println("User doesn't exist!\n");
        }
    }

    public void followersMessageLog(String username){   // user im following log
        List<String> allMsg = jedis.lrange(username, 0, -1);

        for (String message : allMsg) {
            if (message != null) {  System.out.println("-- " + message); }
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        int choice;
        String username;
        String message;
        String curr_user = null;

        MessageSystem ms = new MessageSystem();

        do {
            System.out.println("\n---------- Current user is: " + curr_user + "\n");
             
            System.out.print("1) Create user"
                         + "\n2) Login/Change user"
                         + "\n3) Follow user"
                         + "\n4) Unfollow user"
                         + "\n5) Send message"
                         + "\n6) Read received messages"
                         + "\n7) Exit"
                         + "\n8) Clear DB"
                         + "\nOPTION: ");
            choice = sc.nextInt();

            switch(choice){

                case 1:
                    // create user
                    System.out.print("Username: ");
                    username = sc.next();
                    if (ms.saveUser(username) != 0) {
                       curr_user = username; 
                    }
                    
                    break;

                case 2:
                    // login 
                    System.out.print("Username: ");
                    username = sc.next();
                    if (ms.LoginUser(username) == 1){
                        curr_user = username;
                    }
                    break;

                case 3: 
                    //follow user (create list that saves user1 and all users he follows)
                    System.out.print("User you want to follow: ");
                    username = sc.next();
                    ms.followUser(curr_user, username);
                    break;
                
                case 4: 
                    //unfollow user
                    System.out.print("User you want to unfollow: ");
                    username = sc.next();
                    ms.unfollowUser(curr_user, username);
                    break;

                case 5:
                    //send message and people who follow you see it
                    System.out.print("Message: ");
                    message = sc.next();
                    ms.MyMessagegLog(curr_user, Arrays.asList(message));
                    break;
                
                case 6:
                    //select user to read all their messages (create message log for each user)
                    System.out.print("User you want to see message log: ");
                    username = sc.next();
                    ms.followersMessageLog(username);
                    break;

                case 7: 
                    System.out.println("See ya!");
                    System.exit(0);
                    break;
                
                case 8:
                    ms.jedis.flushDB();
                    System.out.println("Database successfully cleared!\n");
                    curr_user=null;
                    break;
                
                default:
                    System.out.println(choice + " is not a valid option! Please select another.\n");
            } 
    
        } while(choice != 7);

        sc.close();
    }
} 
