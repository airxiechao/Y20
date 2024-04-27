package com.airxiechao.y20.auth.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.pojo.config.AuthConfig;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.ConsulConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.Rest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(AuthRestServer.class);
    private static final AuthConfig config = ConfigFactory.get(AuthConfig.class);
    private static final ConsulConfig consulConfig = ConfigFactory.get(CommonConfig.class).getConsul();

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final AuthRestServer instance = new AuthRestServer();

    private IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);

    public static AuthRestServer getInstance() {
        return instance;
    }

    private AuthRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                (token, scope, item, mode) -> accessTokenBiz.validateAccessToken(token, scope, item, mode));

        // consul
        this.registerConsul(consulConfig.getHost(), consulConfig.getPort(), 10, "y20-backend-");

        // rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }
}