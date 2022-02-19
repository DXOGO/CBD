package com.dxogo;
import redis.clients.jedis.Jedis;

public class Forum {
    private Jedis jedis;
    public Forum(){
    this.jedis = new Jedis();
    System.out.println(jedis.info());
}
    public static void main(String[] args) {
        new Forum();
    }
}
