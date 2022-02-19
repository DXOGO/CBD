package com.dxogo;

import redis.clients.jedis.Jedis;
import java.io.File;
import java.util.Scanner;
import java.util.Set;

public class autoComplete {
    
    private Jedis jedis;
    private String key;

    public autoComplete() {
        this.jedis = new Jedis();
        this.jedis.flushDB();
        this.key = "users";
        saveUsersFromFile("names.txt");
    }

    private void saveUsersFromFile(String file) {

        try {
            File f = new File(file);
            Scanner sc = new Scanner(f);
            String line = sc.nextLine().trim(); 
            while (sc.hasNextLine()){
                line = sc.nextLine().trim(); 

                if (!line.equals("")){
                   this.jedis.zadd(this.key, 0, line); 
                }
            }

            sc.close();

        } catch (Exception e) {
            System.err.println("Error reading file");
            System.exit(0);
        }
    }

    public Set<String> autoSearchUser(String search) {
        return this.jedis.zrangeByLex(this.key, "[" + search, "[" + search + Character.MAX_VALUE);
    }


    public static void main(String[] args) {
        autoComplete auto = new autoComplete();
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Search for ('Enter' for quit): ");
        String input = sc.nextLine();

        while (!input.equals("")) {

            auto.autoSearchUser(input).stream().forEach(System.out::println);

            System.out.print("Search for ('Enter' for quit): ");
            input = sc.nextLine();

        }

        sc.close();
        System.exit(0);
    }
    
}
