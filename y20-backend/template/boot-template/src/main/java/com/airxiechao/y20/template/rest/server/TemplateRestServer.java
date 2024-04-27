package com.airxiechao.y20.template.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.ConsulConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.template.pojo.config.TemplateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRestServer.class);
    private static final TemplateConfig config = ConfigFactory.get(TemplateConfig.class);
    private static final ConsulConfig consulConfig = ConfigFactory.get(CommonConfig.class).getConsul();

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final TemplateRestServer instance = new TemplateRestServer();

    public static TemplateRestServer getInstance() {
        return instance;
    }

    private TemplateRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // register consul
        this.registerConsul(consulConfig.getHost(), consulConfig.getPort(), 10, "y20-backend-");

        // register rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }
}