package com.airxiechao.y20.common.core.db;

import com.airxiechao.axcboot.core.annotation.IMongoDb;
import com.airxiechao.axcboot.core.db.mongo.MongoDbReg;
import com.airxiechao.axcboot.storage.db.mongodb.MongoDbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MongoDb extends MongoDbReg {

    private static final Logger logger = LoggerFactory.getLogger(MongoDb.class);

    private static final MongoDb instance = new MongoDb();
    public static MongoDb getInstance() {
        return instance;
    }
    public static <T> T get(Class<T> interfaceCls){
        return getMongoDbImplProxy(()-> instance, interfaceCls);
    }

    private MongoDb(){
        super(Meta.getProjectPackageName(), new Function<>(){
            private Map<String, MongoDbManager> dbMap = new ConcurrentHashMap<>();

            @Override
            public MongoDbManager apply(Class interfaceCls) {
                IMongoDb annotation = AnnotationUtil.getClassAnnotation(interfaceCls, IMongoDb.class);
                String configFilePath = annotation.value();
                if(StringUtil.isBlank(configFilePath)){
                    configFilePath = "mongodb-y20-base.xml";
                }

                if(dbMap.containsKey(configFilePath)){
                    return dbMap.get(configFilePath);
                }else{
                    MongoDbManager dbManager = MongoDbManagerUtil.createMongoDbManager(new JavaResourceFs(), configFilePath);
                    dbMap.put(configFilePath, dbManager);
                    return dbManager;
                }
            }
        });

        registerProcedureIfExists();
    }


}