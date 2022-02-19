package com.dxogo;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import redis.clients.jedis.Jedis;

public class SimplePostHash {
    
    private Jedis jedis;
    public static String USERSHASH = "usershash"; // Key set for users' name
    
    public SimplePostHash() {
        this.jedis = new Jedis();
        jedis.flushDB();
    }

    public void saveUser(String username, Map<String, String> map) {
        jedis.hmset(username, map);
    }

    public Map<String, String> getUser(String user) {
        return jedis.hgetAll(user);
    }

    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }

    public static void main(String[] args) {
        SimplePostHash board = new SimplePostHash();
        // Set some users
        String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        Map<String, String> map = new HashMap<String, String>();
        map.put("Ana", "Anadia");
        map.put("Pedro", "Porto");
        map.put("Maria", "Madrid");
        map.put("Luis", "Lisboa");
        
        for (String user : users)
            board.saveUser(user, map);
        board.getAllKeys().stream().forEach(System.out::println);
        //board.getUser().stream().forEach(System.out::println);

    }
}

