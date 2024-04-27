package com.airxiechao.y20.common.core.db;

import com.airxiechao.axcboot.core.annotation.IInfluxDb;
import com.airxiechao.axcboot.core.db.influxdb.InfluxDbReg;
import com.airxiechao.axcboot.storage.db.influxdb.InfluxDbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InfluxDb extends InfluxDbReg {

    private static final Logger logger = LoggerFactory.getLogger(InfluxDb.class);

    private static final InfluxDb instance = new InfluxDb();
    public static InfluxDb getInstance() {
        return instance;
    }
    public static <T> T get(Class<T> interfaceCls){
        return getInfluxDbImplProxy(()-> instance, interfaceCls);
    }

    private InfluxDb(){
        super(Meta.getProjectPackageName(), new Function<>(){
            private Map<String, InfluxDbManager> dbMap = new ConcurrentHashMap<>();

            @Override
            public InfluxDbManager apply(Class interfaceCls) {
                IInfluxDb annotation = AnnotationUtil.getClassAnnotation(interfaceCls, IInfluxDb.class);
                String configFilePath = annotation.value();
                if(StringUtil.isBlank(configFilePath)){
                    configFilePath = "influxdb-y20.xml";
                }

                if(dbMap.containsKey(configFilePath)){
                    return dbMap.get(configFilePath);
                }else{
                    InfluxDbManager dbManager = InfluxDbManagerUtil.createInfluxDbManager(new JavaResourceFs(), configFilePath);
                    dbMap.put(configFilePath, dbManager);
                    return dbManager;
                }
            }
        });

        registerProcedureIfExists();
    }


}