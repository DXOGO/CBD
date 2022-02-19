package com.dxogo;

import redis.clients.jedis.Jedis;
import java.io.File;
import java.util.Scanner;
import java.util.stream.*;
import java.util.Set;

public class autoCompleteCSV {
    private Jedis jedis;
    private String users_keys;
    private String lexical_users_key;

    public autoCompleteCSV() {
        this.jedis = new Jedis();
        this.users_keys = "users";
        this.lexical_users_key = "lexical_order_users";
        saveUsersFromFile("nomes-pt-2021.csv");
    }

    public void saveUsersFromFile(String filename) {
        try {
            File fl = new File(filename);
            Scanner sc = new Scanner(fl);
            String raw;
            String[] line;

            while (sc.hasNextLine()) {
                raw = sc.nextLine().trim();
                if (!raw.equals("")) {
                    line = raw.split(";");
                    this.jedis.zadd(this.users_keys, Integer.parseInt(line[1]), line[0]);
                    this.jedis.zadd(this.lexical_users_key, 0, line[0]);
                }
            }

            sc.close();

        } catch (Exception e) {
            System.err.println("Error reading from file");
            e.printStackTrace();
            System.exit(0);

        }

    }

    public Stream<String> getUsersStartedWith(String search) {
        Set<String> autocomplete = this.jedis.zrangeByLex(this.lexical_users_key, "[" + search,
                "[" + search + Character.MAX_VALUE);
        Set<String> userByScore = this.jedis.zrevrangeByScore(this.users_keys, Integer.MAX_VALUE, Integer.MIN_VALUE);

        return userByScore.stream().filter(user -> autocomplete.contains(user));

    }

    public static void main(String[] args) {
        autoCompleteCSV auto = new autoCompleteCSV();
        Scanner sc = new Scanner(System.in);
        String input = "init";

        while (!input.equals("")) {

            auto.getUsersStartedWith(input).forEach(System.out::println);

            System.out.print("Search for ('Enter' for quit): ");
            input = sc.nextLine();

        }

        sc.close();
        System.exit(0);
    }
}