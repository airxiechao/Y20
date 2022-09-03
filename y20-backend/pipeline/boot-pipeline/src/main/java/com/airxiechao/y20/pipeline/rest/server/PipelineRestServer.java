package com.airxiechao.y20.pipeline.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.pipeline.pojo.config.PipelineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRestServer.class);
    private static final PipelineConfig config = ConfigFactory.get(PipelineConfig.class);

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final PipelineRestServer instance = new PipelineRestServer();

    public static PipelineRestServer getInstance() {
        return instance;
    }

    private PipelineRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // register consul
        this.registerConsul(10, "y20-backend-");

        // register rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

        // register event sinks
        EventBus.getInstance().registerSinks(Meta.getModulePackageName(this.getClass()));
    }
}
