package com.airxiechao.y20.quota.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.quota.pojo.config.QuotaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuotaRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(QuotaRestServer.class);
    private static final QuotaConfig config = ConfigFactory.get(QuotaConfig.class);

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final QuotaRestServer instance = new QuotaRestServer();

    public static QuotaRestServer getInstance() {
        return instance;
    }

    private QuotaRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // register consul
        this.registerConsul(10, "y20-backend-");

        // register rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }
}