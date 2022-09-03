package com.airxiechao.y20.agent.rest.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.agent.pojo.config.AgentConfig;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentRestServer extends RestServer {
    private static final Logger logger = LoggerFactory.getLogger(AgentRestServer.class);

    private static final AgentConfig config = ConfigFactory.get(AgentConfig.class);
    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final AgentRestServer instance = new AgentRestServer();
    public static AgentRestServer getInstance() {
        return instance;
    }

    private AgentRestServer() {
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
        this.registerConsul(10, "y20-backend-");

        // rest
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();

    }

}