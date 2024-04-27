package com.airxiechao.y20.common.core.db;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.core.db.DbReg;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public class Db extends DbReg {

    private static final Logger logger = LoggerFactory.getLogger(Db.class);

    private static final Db instance = new Db();
    public static Db getInstance() {
        return instance;
    }
    public static <T> T get(Class<T> interfaceCls){
        return getDbImplProxy(()-> instance, interfaceCls);
    }

    private Db(){
        super(Meta.getProjectPackageName(), new Function<>(){
            private Map<String, DbManager> dbManagerMap = new ConcurrentHashMap<>();

            @Override
            public DbManager apply(Class interfaceCls) {
                IDb annotation = AnnotationUtil.getClassAnnotation(interfaceCls, IDb.class);
                String configFilePath = annotation.value();
                if(StringUtil.isBlank(configFilePath)){
                    configFilePath = "mybatis-y20-base.xml";
                }

                if(dbManagerMap.containsKey(configFilePath)){
                    return dbManagerMap.get(configFilePath);
                }else{
                    DbManager dbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), configFilePath);
                    dbManagerMap.put(configFilePath, dbManager);
                    return dbManager;
                }
            }
        });

        registerProcedureIfExists();
    }


}

