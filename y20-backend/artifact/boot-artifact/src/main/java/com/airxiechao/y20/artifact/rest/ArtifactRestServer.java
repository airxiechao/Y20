package com.airxiechao.y20.artifact.rest;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.artifact.pojo.config.ArtifactConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactRestServer.class);
    private static final ArtifactConfig config = ConfigFactory.get(ArtifactConfig.class);

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final ArtifactRestServer instance = new ArtifactRestServer();

    public static ArtifactRestServer getInstance() {
        return instance;
    }

    private ArtifactRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // register consul
        this.registerConsul(10, "y20-backend-");

        // register rest
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();

        // register event sinks
        EventBus.getInstance().registerSinks(Meta.getModulePackageName(this.getClass()));
    }
}
