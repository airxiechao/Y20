package com.airxiechao.y20.monitor.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.monitor.pojo.config.MonitorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorRestServer.class);

    public static final String NAME = ConfigFactory.get(MonitorConfig.class).getName();;
    public static final int PORT = ConfigFactory.get(MonitorConfig.class).getPort();

    private static final MonitorRestServer instance = new MonitorRestServer();
    public static MonitorRestServer getInstance() {
        return instance;
    }

    private MonitorRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // consul
        this.registerConsul(10, "y20-backend-");

        // rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }

}
