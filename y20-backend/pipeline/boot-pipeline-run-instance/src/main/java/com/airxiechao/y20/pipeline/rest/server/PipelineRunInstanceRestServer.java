package com.airxiechao.y20.pipeline.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.pipeline.pojo.config.PipelineRunInstanceConfig;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineInjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineRunInstanceRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunInstanceRestServer.class);
    private static final PipelineRunInstanceConfig config = ConfigFactory.get(PipelineRunInstanceConfig.class);

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final PipelineRunInstanceRestServer instance = new PipelineRunInstanceRestServer();

    public static PipelineRunInstanceRestServer getInstance() {
        return instance;
    }

    private PipelineRunInstanceRestServer() {
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

        // provide service tag
        Global.getInstance().provide(EnumPipelineInjectName.PIPELINE_RUN_INSTANCE_REST_SERVER, getUuid());

    }
}
