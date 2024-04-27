package com.airxiechao.y20.project.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.ConsulConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.project.pojo.config.ProjectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRestServer.class);
    private static final ConsulConfig consulConfig = ConfigFactory.get(CommonConfig.class).getConsul();

    public static final String NAME = ConfigFactory.get(ProjectConfig.class).getName();
    public static final int PORT = ConfigFactory.get(ProjectConfig.class).getPort();

    private static final ProjectRestServer instance = new ProjectRestServer();
    public static ProjectRestServer getInstance() {
        return instance;
    }

    private ProjectRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken);

        // consul
        this.registerConsul(consulConfig.getHost(), consulConfig.getPort(), 10, "y20-backend-");

        // rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }

}