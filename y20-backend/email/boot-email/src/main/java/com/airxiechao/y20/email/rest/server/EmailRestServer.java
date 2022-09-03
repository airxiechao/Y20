package com.airxiechao.y20.email.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.email.pojo.config.EmailConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(EmailRestServer.class);

    public static final String NAME = ConfigFactory.get(EmailConfig.class).getName();;
    public static final int PORT = ConfigFactory.get(EmailConfig.class).getPort();

    private static final EmailRestServer instance = new EmailRestServer();
    public static EmailRestServer getInstance() {
        return instance;
    }

    private EmailRestServer() {
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