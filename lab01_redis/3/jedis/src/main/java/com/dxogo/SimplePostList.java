package com.dxogo;

import java.util.Set;
import java.util.List;
import redis.clients.jedis.Jedis;

public class SimplePostList {

    private Jedis jedis;
    public static String USERSLIST = "userslist"; // Key set for users' name
    
    public SimplePostList() {
        this.jedis = new Jedis();
        jedis.flushDB();
    }

    public void saveUserLeft(String user) {
        jedis.lpush(USERSLIST, user);
    }

    public void saveUserRight(String user) {
        jedis.rpush(USERSLIST, user);
    }

    public List<String> getUser() {
        return jedis.lrange(USERSLIST, 0, -1);
    }

    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }

    public static void main(String[] args) {
        SimplePostList board = new SimplePostList();
        // set some users
        String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        for (String user: users)
            board.saveUserLeft(user);
        board.getAllKeys().stream().forEach(System.out::println);
        board.getUser().stream().forEach(System.out::println);
    }     
}
