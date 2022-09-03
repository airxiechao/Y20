package com.airxiechao.y20.sql;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.pojo.config.MongoDbConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

public class CreateMongoDdMain {
    public static void main(String[] args){

        // database: y20-pipeline-run-log
        createLogDatabase();

        // database: y20-activity
        createActivityDatabase();

    }

    public static void createLogDatabase(){
        MongoDbConfig config = ConfigFactory.get(MongoDbConfig.class, "mongodb-y20-log.yml");
        MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%s@%s:%d/%s",
                config.getUsername(), config.getPassword(),
                config.getHost(), config.getPort(), config.getDatabase()));

        MongoDatabase database =  mongoClient.getDatabase(config.getDatabase());

        String collectionName = "log";
        try {
            database.createCollection(collectionName);
        }catch (Exception e){}
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.createIndex(Indexes.ascending("pipelineRunInstanceUuid"));
        collection.createIndex(Indexes.ascending("stepRunInstanceUuid"));
        collection.createIndex(Indexes.ascending("time"));

        mongoClient.close();
    }

    public static void createActivityDatabase(){
        MongoDbConfig config = ConfigFactory.get(MongoDbConfig.class, "mongodb-y20-activity.yml");
        MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%s@%s:%d/%s",
                config.getUsername(), config.getPassword(),
                config.getHost(), config.getPort(), config.getDatabase()));

        MongoDatabase database =  mongoClient.getDatabase(config.getDatabase());

        String collectionName = "activity";
        try {
            database.createCollection(collectionName);
        }catch (Exception e){}
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.createIndex(Indexes.ascending("userId", "time"));

        mongoClient.close();
    }
}
