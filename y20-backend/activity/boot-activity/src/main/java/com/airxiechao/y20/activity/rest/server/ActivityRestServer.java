package com.airxiechao.y20.activity.rest.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.activity.pojo.config.ActivityConfig;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.ConsulConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityRestServer extends RestServer {
    private static final Logger logger = LoggerFactory.getLogger(ActivityRestServer.class);

    private static final ActivityConfig config = ConfigFactory.get(ActivityConfig.class);
    private static final ConsulConfig consulConfig = ConfigFactory.get(CommonConfig.class).getConsul();
    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final ActivityRestServer instance = new ActivityRestServer();

    public static ActivityRestServer getInstance() {
        return instance;
    }

    private ActivityRestServer() {
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
        rest.registerHandlerIfExists();

    }
}
