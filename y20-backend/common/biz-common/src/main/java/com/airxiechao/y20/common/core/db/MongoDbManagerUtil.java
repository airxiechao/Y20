package com.airxiechao.y20.common.core.db;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.db.mongodb.MongoDbManager;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.template.StringTemplate;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.MongoDbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MongoDbManagerUtil {
    private static final Logger logger = LoggerFactory.getLogger(MongoDbManagerUtil.class);
    private static final CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);

    public static MongoDbManager createMongoDbManager(IFs configFs, String configFilePath){
        try(InputStream inputStream = configFs.getInputStream(configFilePath)){
            String configContent = StreamUtil.readString(inputStream, StandardCharsets.UTF_8);

            // render common config
            StringTemplate template = new StringTemplate(configContent);
            configContent = template.render(ModelUtil.toMap(commonConfig));

            MongoDbConfig config = ConfigFactory.getByContent(MongoDbConfig.class, configFilePath, configContent);
            MongoDbManager dbManager = new MongoDbManager(
                    config.getHost(), config.getPort(),
                    config.getUsername(), config.getPassword(),
                    config.getDatabase());

            return dbManager;
        } catch (Exception e){
            throw new RuntimeException(
                    String.format("create mongodb manager [%s] error", configFilePath), e);
        }
    }
}
