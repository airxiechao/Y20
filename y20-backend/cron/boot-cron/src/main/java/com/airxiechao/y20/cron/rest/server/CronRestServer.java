package com.airxiechao.y20.cron.rest.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.cron.pojo.config.CronConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronRestServer extends RestServer {
    private static final Logger logger = LoggerFactory.getLogger(CronRestServer.class);

    private static final CronConfig config = ConfigFactory.get(CronConfig.class);
    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final CronRestServer instance = new CronRestServer();

    public static CronRestServer getInstance() {
        return instance;
    }

    private CronRestServer() {
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
