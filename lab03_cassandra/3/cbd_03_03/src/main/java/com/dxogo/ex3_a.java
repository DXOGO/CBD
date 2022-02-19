package com.dxogo;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ex3_a {

    // create cassandra session
    private static Session session;

    public static void main(String[] args) {
     
         // connect to cassandra db
         try {
            Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

            session = cluster.connect("cbd_03_02");

            // Desenvolva uma pequena aplicação Java que demonstre a inserção, edição e pesquisa de registos na base de dados.
            
            // INSERTING VIDEO
            System.out.println("\n### Inserting video ###");
            Set<String> tagSet = Stream.of("'onetag'", "'twotag'").collect(Collectors.toSet());
            Set<String> followersEmail = Stream.of("'d9@ua.pt'", "'d10@ua.pt'")
                            .collect(Collectors.toSet());
            insertVideo(99, "D8", "d8@ua.pt","hello my friends", tagSet, followersEmail, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            searchVideoById(99);
            
            // SEARCH ALL VIDEOS
            System.out.println("\n### Searching all videos ###");
            searchAllVideos();

            // EDITING TITLE(NAME) OF EXISTING VIDEO
            System.out.println("\n### Updating existing video title with ID=99 ");
            editVideo(99, "name", "new title for id 99", "d8@ua.pt", "2021-12-19");

            // EDITING DESCRIPTION OF EXISTING VIDEO
            System.out.println("\n### Updating existing video description with ID=99 ");
            editVideo(99, "description", "new description for id 99", "d8@ua.pt", "2021-12-19");

            // SEARCH VIDEO BY BY
            System.out.println("\n### Searching video with ID=99 ###");
            searchVideoById(99);

            session.close();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error connecting to Cassandra database: " + e);
            System.exit(1);
        }
    }


    // simple function to search ALL videos from database
    private static void searchAllVideos() {
        try {
            String cassandra_statement = "SELECT * FROM video";
            for (Row row : session.execute(cassandra_statement)) {
                System.out.println(row.toString());
            }
        }catch (Exception e){
            System.err.println("Error reading from Cassandra database: " + e);
        }
    }

    // simple function to search videos specifying an id
    public static void searchVideoById(Integer id){
        try {
            String cassandra_statement = "SELECT * FROM video WHERE id = " + id;
            for (Row row : session.execute(cassandra_statement)) {
                System.out.println(row.toString());
            }
        }catch (Exception e){
            System.err.println("Error reading from Cassandra database: " + e);
        }

    }


    // Insert data to the table video in db
    public static void insertVideo(Integer id, String name, String uploader_email, String description, Set<String> tag, Set<String> followers_emails, String upload_date){
        try {
            Integer count = 0;
            String tagString = "{";
            String followersEmailsString = "{";
            for (String t : tag) {
                if (count == 0) {
                    tagString += t;
                    count = 1;
                }
                else
                    tagString += ", " + t;
            }
            count=0;

            for (String e : followers_emails) {
                if (count == 0) {
                    followersEmailsString += e;
                    count = 1;
                }
                else
                    followersEmailsString += ", " + e;
            }
            tagString += "}";
            followersEmailsString += "}";


            StringBuilder sb = new StringBuilder("INSERT INTO ")
                    .append("video").append("(id, name, uploader_email, description, tag, followers_emails, upload_date) ")
                    .append("VALUES (").append(id).append(", '" + name + "'").append(", '" + uploader_email + "'").append(", '" + description + "'")
                    .append("," + tagString).append("," + followersEmailsString).append(", '" + upload_date + "');");
            String query = sb.toString();
            session.execute(query);
            System.out.println("Video successfully uploaded!");
        }catch (Exception e){
            System.err.println("Error inserting into Cassandra database: " + e);
        }

    }

    // edit existing video
    public static void editVideo(Integer id, String item_to_change, String newitem, String uploader_email, String date){
        try {

            StringBuilder sb = new StringBuilder("UPDATE video SET ").append(item_to_change + "=")
                    .append("'" + newitem + "'").append(" WHERE id = ").append(id)
                    .append(" AND uploader_email = ").append("'"+uploader_email+"'")
                    .append(" AND upload_date = ").append("'" + date + "';");
            String query = sb.toString();
            session.execute(query);
            System.out.println("Video successfully updated!");
        }catch (Exception e){
            System.err.println("Error updating into Cassandra database: " + e);
        }

    }
}
