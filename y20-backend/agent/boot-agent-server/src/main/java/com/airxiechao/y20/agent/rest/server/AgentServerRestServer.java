package com.airxiechao.y20.agent.rest.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.agent.pojo.config.AgentServerConfig;
import com.airxiechao.y20.agent.pojo.constant.EnumAgentInjectName;
import com.airxiechao.y20.agent.rest.api.IServiceAgentServerRest;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.ConsulConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentServerRestServer extends RestServer {
    private static final Logger logger = LoggerFactory.getLogger(AgentServerRestServer.class);

    private static final AgentServerConfig config = ConfigFactory.get(AgentServerConfig.class);
    private static final ConsulConfig consulConfig = ConfigFactory.get(CommonConfig.class).getConsul();
    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final AgentServerRestServer instance = new AgentServerRestServer();
    public static AgentServerRestServer getInstance() {
        return instance;
    }

    private AgentServerRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                (token, scope, item, mode) -> {
                    ValidateAccessTokenParam validation = new ValidateAccessTokenParam(
                            token, scope, item, mode
                    );
                    Response resp = ServiceRestClient.get(IServiceAuthRest.class).validateAccessToken(validation);

                    return resp.isSuccess();
                });

        // consul
        this.registerConsul(consulConfig.getHost(), consulConfig.getPort(), 10, "y20-backend-");

        // rest
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists(new Class[]{
                IServiceAgentServerRest.class
        });

        // provide service tag
        Global.getInstance().provide(EnumAgentInjectName.AGENT_REST_SERVER_UUID, getUuid());
    }

}
