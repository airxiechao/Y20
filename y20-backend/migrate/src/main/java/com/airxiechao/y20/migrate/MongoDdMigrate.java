package com.airxiechao.y20.migrate;

import com.airxiechao.axcboot.storage.db.mongodb.MongoDbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.y20.common.core.db.MongoDbManagerUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDdMigrate {
    private static final Logger logger = LoggerFactory.getLogger(MongoDdMigrate.class);

    public static void main(String[] args){
        migrateLogDatabase();
        migrateActivityDatabase();
    }

    public static void migrateLogDatabase(){
        MongoDbManager mongoDbManager = MongoDbManagerUtil.createMongoDbManager(new JavaResourceFs(), "mongodb-y20-log.yml");
        MongoClient mongoClient = mongoDbManager.getClient();
        MongoDatabase database =  mongoDbManager.getDatabase();

        String collectionName = "log";
        for (String name : database.listCollectionNames()) {
            if(name.equals(collectionName)){
                return;
            }
        }

        logger.info("create mongodb collection [{}/{}]", database.getName(), collectionName);

        try {
            database.createCollection(collectionName);

            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.createIndex(Indexes.ascending("pipelineRunInstanceUuid"));
            collection.createIndex(Indexes.ascending("stepRunInstanceUuid"));
            collection.createIndex(Indexes.ascending("time"));
        }catch (Exception e){
            logger.error("create mongodb collection error", e);
        }

        mongoClient.close();
    }

    public static void migrateActivityDatabase(){
        MongoDbManager mongoDbManager = MongoDbManagerUtil.createMongoDbManager(new JavaResourceFs(), "mongodb-y20-activity.yml");
        MongoClient mongoClient = mongoDbManager.getClient();
        MongoDatabase database =  mongoDbManager.getDatabase();

        String collectionName = "activity";
        for (String name : database.listCollectionNames()) {
            if(name.equals(collectionName)){
                return;
            }
        }

        logger.info("create mongodb collection [{}/{}]", database.getName(), collectionName);

        try {
            database.createCollection(collectionName);

            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.createIndex(Indexes.ascending("userId", "time"));
        }catch (Exception e){
            logger.error("create mongodb collection error", e);
        }

        mongoClient.close();
    }
}
