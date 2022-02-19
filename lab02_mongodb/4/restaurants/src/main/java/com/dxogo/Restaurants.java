package com.dxogo;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import static com.mongodb.client.model.Aggregates.group;
import com.mongodb.client.model.Indexes;

import org.bson.Document;
import org.bson.conversions.Bson;


public class Restaurants {
       
    // alinea a) ----------------------------------------
    public static void insert(MongoCollection<Document> collection, Document doc){
        try{

            collection.insertOne(doc);

        } catch (MongoException e){
            System.err.println("Unable to insert due to an error: " + e);
        }

    }

    public static void update(MongoCollection<Document> collection, String data, String old, String _new){
        try{

            collection.updateOne(eq(data, old), set(data, _new));

        } catch(Exception e){
            System.out.printf("Error: ", e);
            System.out.println("No document was updated!\n");
        }

    }

    public static void query(MongoCollection<Document> collection) {
        FindIterable<Document> docs = collection.find();
        for (Object doc : docs) { 
            System.out.println(doc);
        }
    }

    // alinea b) ----------------------------------------
    public static void searchWithFilter(MongoCollection<Document> collection, Bson filter){
        try {
            FindIterable<Document> docs = collection.find(filter);
            for (Document d : docs) {
                System.out.println(d.toJson());
            }
        }catch (Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }

    }

    public static void createIndex(MongoCollection<Document> collection, String s) {
        try {
            collection.createIndex(Indexes.ascending(s));

        } catch (Exception e) {
            System.err.println("Error creating ascending index on " + s + "field in MongoDB collection: " + e);
        }
    }

    public static void createIndexByText(MongoCollection<Document> collection, String s) {
        try {
            collection.createIndex(Indexes.text((s)));

        } catch (Exception e) {
            System.err.println("Error creating text index on " + s + "field in MongoDB collection: " + e);
        }
    }

    // alinea c) ----------------------------------------

    // 1. Liste todos os documentos da coleção.
    public static List<String> allRest(MongoCollection<Document> collection){

        List<String> result = new ArrayList<String>();

        try{
           
            MongoCursor<Document> cursor = collection.find().iterator();
            
            while (cursor.hasNext()) {
                result.add(cursor.next().toString());
            }
 
        } catch(Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }

        return result;
    }

            
        // 5. Apresente os primeiros 15 restaurantes localizados no Bronx, ordenados por ordem crescente de nome.
        public static List<String> first15Bronx(MongoCollection<Document> collection){
            
            List<String> result = new ArrayList<String>();

            try{

                Document d = new Document("localidade","Bronx");
                FindIterable<Document> find = collection.find(d).sort(new Document("nome", 1)).limit(15);
                MongoCursor<Document> cursor = find.cursor();
            
                while (cursor.hasNext()) {
                    result.add(cursor.next().toString());
                }
            
            } catch(Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }

        return result;
    }

           


    // 6. Liste todos os restaurantes que tenham pelo menos um score superior a 85.
    public static List<String> allRestScore85(MongoCollection<Document> collection){

        List<String> result = new ArrayList<String>();

        try{

            Document d = new Document("grades.score",
                                new Document("$gte", 85));

            FindIterable<Document> find = collection.find(d);
            MongoCursor<Document> cursor = find.cursor();
            
            while (cursor.hasNext()) {
                result.add(cursor.next().toString());
            }

        } catch(Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }
        
        return result;

    }

    // 20. Liste todos os restaurantes cuja média dos score é superior a 30. 
    public static List<String> allRestAvgScore30(MongoCollection<Document> collection){
        
        List<String> result = new ArrayList<String>();

        try{
            Document d = new Document(
                "$addFields", 
                    new Document("avg_score", 
                    new Document("$avg","$grades.score")));
                    
            Document dd = new Document("$match", 
                                new Document("avg_score", 
                                new Document("$gt",30)));

            List<Document> l = new ArrayList<>();
            l.add(d);
            l.add(dd);

            AggregateIterable<Document> find = collection.aggregate(l);
            MongoCursor<Document> cursor = find.cursor();
            
            while (cursor.hasNext()) {
                result.add(cursor.next().toString());
            }

        } catch(Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }
        
        return result;
    }

    private static List<String> allRestPtInfo(MongoCollection<Document> collection) {
        
        List<String> result = new ArrayList<String>();

        try{

            Document d = new Document(
                "$addFields", 
                    new Document("sum_score", 
                    new Document("$sum","$grades.score"))
        );

            Document dd = new Document(
                    "$match",
                        new Document("sum_score",
                        new Document("$gt", 50)));

            Document ddd = new Document();
            ddd.append("$match", new Document("gastronomia", "Portuguese"));
            
            Document dddd = new Document();
            dddd.append("$match", new Document("address.coord.0", new Document("$lt",-60)));


            List<Document> l = new ArrayList<>();
            l.add(d);
            l.add(dd);
            l.add(ddd);
            l.add(dddd);

            AggregateIterable<Document> find = collection.aggregate(l);
            MongoCursor<Document> cursor = find.cursor();
            
            while (cursor.hasNext()) {
                result.add(cursor.next().toString());
            }

        } catch(Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }
        
        return result;
    }

    // alinea d) ----------------------------------------

    // localidades distintas
    public static int countLocalidades(MongoCollection<Document> collection){

        int count = 0;

        try{
            DistinctIterable<String> documents = collection.distinct("localidade", String.class);

            for (String document : documents){ count++; }
 
        } catch(Exception e){
            System.err.println("Error getting countLocalidades in MongoDB collection: " + e);
        }

        return count;
    }

    // count restaurants by location
    public static Map<String, Integer> countRestByLocalidade(MongoCollection<Document> collection){
        Map<String, Integer> result = new HashMap<>();

        try {
            AggregateIterable<Document> docs = collection.aggregate(Arrays.asList(group("$localidade", Accumulators.sum("sum", 1))));

            for (Document d : docs) {
                result.put(d.get("_id").toString(), (int) (d.get("sum")));
            }

        } catch (Exception e) {
            System.err.println("Error getting countRestByLocalidade in MongoDB collection: " + e);
        }

        return result;
    }


    // find restaurants name containing 's'
    public static List<String> getRestWithNameCloserTo(MongoCollection<Document> collection, String s){
    
        List<String> result = new ArrayList<String>();

        try {
            FindIterable<Document> docs = collection.find(regex("nome", String.format("(%s)", s)));
            
            for (Document doc : docs) {
                result.add((String) doc.get("nome"));
            }

        }catch (Exception e){
            System.err.println("Error reading from MongoDB collection: " + e);
        }

        return result;

    }


    public static void main(String[] args) {

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");

        // alinea a) ----------------------------------------
        /*
        Document doc = new Document("address", new Document("building", "9322").append("coord", Arrays.asList(-77.849384399, 42.688197))
                                                                               .append("rua", "University Street")
                                                                               .append("zipcode", "3810"))
                            .append("localidade", "Aveiro")
                            .append("gastronomia", "Portuguesa")
                            .append("grades", Arrays.asList(new Document("date", "2013-12-27T00:00:00Z").append("grade", "A").append("score", 10) ))
                            .append("nome", "AFUAV")
                            .append("restaurant_id", "42358514");   
        
        insert(collection, doc);
        update(collection, "nome", "AFUAV","AFFUAV");
        query(collection, new Document("localidade","Aveiro"));
        */

        // alinea b) -----------------------------------------
        /*
        createIndex(collection, "gastronomia");
        createIndex(collection, "localidade");
        createIndexByText(collection, "nome");
        /*

        /*
        searchWithFilter(collection, eq("localidade", "Brooklyn"));
        searchWithFilter(collection, eq("gastronomia", "Chinese"));
        */

        // alinea c) ------------------------------------------

        // 1. Liste todos os documentos da coleção.
        /*
        System.out.println("\n Liste todos os documentos da coleção.");
        List<String> map = allRest(collection);
        for (String rest : map) {
            System.out.println(rest + "\n");
        }
        */

        // 5. Apresente os primeiros 15 restaurantes localizados no Bronx, ordenados por ordem crescente de nome.
        /*
        List<String> map2 = first15Bronx(collection);
        for (String rest : map2) {
            System.out.println(rest + "\n");
        }
        */

        // 6. Liste todos os restaurantes que tenham pelo menos um score superior a 85
        /*
        System.out.println("\n Liste todos os restaurantes que tenham pelo menos um score superior a 85");
        List<String> map3 = allRestScore85(collection);
        for (String rest : map3) {
            System.out.println(rest + "\n");
        }
        */

        // 20. Liste todos os restaurantes cuja média dos score é superior a 30.
        /*
        System.out.println("\n Liste todos os restaurantes cuja média dos score é superior a 30");
        List<String> map4 = allRestAvgScore30(collection);
        for (String rest : map4) {
            System.out.println(rest + "\n");
        }
        */

        // 21. Indique os restaurantes que têm gastronomia "Portuguese", o somatório de score é superior a 50 e estão numa latitude inferior a -60.
        // System.out.println("\n Indique os restaurantes que têm gastronomia 'Portuguese', o somatório de score é superior a 50 e estão numa latitude inferior a -60");
        // List<String> map5 = allRestPtInfo(collection);
        // for (String rest : map5) {
        //     System.out.println(rest + "\n");
        // }

        // alinea d) ------------------------------------------
        /*
        try {
            FileWriter w = new FileWriter("CBD_L204_98595.txt");

            // count localidades distintas
            w.write("Numero de localidades distintas: " + countLocalidades(collection) + "\n");
            

            // count restaurants by location
            w.write("\nNumero de restaurantes por localidade:");
            Map<String, Integer> map = countRestByLocalidade(collection);
            for (String key : map.keySet()) {
                w.write(" -> " + key + " - " + map.get(key) + "\n");
            }

            // find restaurants name containing 's'    
            w.write("\nNome de restaurantes contendo 'Park' no nome:");
            
            List<String> list = getRestWithNameCloserTo(collection, "Park");
            
            for (String value : list) {
                w.write(" -> " + value + "\n");
            }
            
            System.out.println("Result successfully added to CBD_L204_98595.txt");
            w.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        */
    }
}
