package com.airxiechao.y20.common.core.db;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.template.StringTemplate;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DbManagerUtil {
    private static final CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);

    public static DbManager createDbManager(IFs configFs, String configFilePath){
        try(InputStream inputStream = configFs.getInputStream(configFilePath)){
            String configContent = StreamUtil.readString(inputStream, StandardCharsets.UTF_8);

            // fix mybatis dtd
            URL dtdUrl = DbManagerUtil.class.getClassLoader().getResource("mybatis-3-config.dtd");
            if(null != dtdUrl){
                configContent = configContent.replace("http://mybatis.org/dtd/mybatis-3-config.dtd", dtdUrl.toString());
            }

            // render common config
            StringTemplate template = new StringTemplate(configContent);
            configContent = template.render(ModelUtil.toMap(commonConfig));

            DbManager dbManager = new DbManager(configContent);
            return dbManager;
        } catch (Exception e){
            throw new RuntimeException(
                    String.format("create db manager [%s] error", configFilePath), e);
        }
    }
}
