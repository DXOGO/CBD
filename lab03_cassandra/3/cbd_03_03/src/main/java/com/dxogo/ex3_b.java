package com.dxogo;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class ex3_b {

    // create cassandra session
    private static Session session;

    public static void main(String[] args) {
     
         // connect to cassandra db
         try {
            Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

            session = cluster.connect("cbd_03_02");

            // Reimplemente em Java, quatro queries à sua escolha do exercício 3.02.

            // 1. Os últimos 3 comentários introduzidos para um vídeo
            System.out.println("\n### Os últimos 3 comentários introduzidos para um vídeo (ex. id=2) ###");
            cqlQuery("SELECT * FROM videocomment WHERE video_id = 1 LIMIT 3 ;");

            // 2. Lista das tags de determinado vídeo
            System.out.println("\n### Lista das tags de determinado vídeo (ex. id=2) ###");
            cqlQuery("SELECT tag FROM video WHERE id = 2;");

            // 3. Todos os vídeos com a tag Aveiro;
            // DIDNT HAVE TAG=AVEIRO SO CHANGED TO TAG=FOOTBALL
            System.out.println("\n### 3. Todos os vídeos com a tag Football (changed because I didn't have a tag=Aveiro);");
            cqlQuery("SELECT * FROM tagbyvideo where name = 'Football';");

            session.close();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error connecting to Cassandra database: " + e);
            System.exit(1);
        }
    }

    public static void cqlQuery(String query){
        try {
            for (Row row : session.execute(query)) {
                System.out.println(row.toString());
            }
        }catch (Exception e){
            System.err.println("Error fetching Cassandra database: " + e);
            System.exit(1);
        }

    }

}