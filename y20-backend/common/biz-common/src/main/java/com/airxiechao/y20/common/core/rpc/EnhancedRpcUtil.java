package com.airxiechao.y20.common.core.rpc;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.communication.rpc.util.RpcUtil;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.pojo.exception.ExtractAccessPrincipalException;
import com.airxiechao.y20.auth.pojo.exception.InvalidAccessTokenException;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ExtractAccessPrincipalParam;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnhancedRpcUtil {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRpcUtil.class);

    public static boolean validateAccessToken(String accessToken, String scope, String item, Integer mode){
        ValidateAccessTokenParam param = new ValidateAccessTokenParam(accessToken, scope, item, mode);
        Response resp = ServiceRestClient.get(IServiceAuthRest.class).validateAccessToken(param);

        return resp.isSuccess();
    }

    public static AccessPrincipal extractAccessPrincipal(RpcExchange exchange) throws InvalidAccessTokenException, ExtractAccessPrincipalException {
        String accessToken = RpcUtil.getAuthToken(exchange);
        ExtractAccessPrincipalParam param = new ExtractAccessPrincipalParam(accessToken);
        Response<AccessPrincipal> resp = ServiceRestClient.get(IServiceAuthRest.class).extractAccessPrincipal(param);
        if(!resp.isSuccess()){
            logger.error("extract rpc access principal error: {}", resp.getMessage());

            if(InvalidAccessTokenException.INVALID_ACCESS_TOKEN.equals(resp.getMessage())){
                throw new InvalidAccessTokenException();
            }else{
                throw new ExtractAccessPrincipalException(resp.getMessage());
            }
        }
        AccessPrincipal accessPrincipal = resp.getData();
        return accessPrincipal;
    }

    public static <T> T getObjectParamWithAuth(RpcExchange exchange, Class<T> tClass) throws InvalidAccessTokenException, ExtractAccessPrincipalException {
        // get user id
        AccessPrincipal accessPrincipal = extractAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        exchange.getPayload().put("userId", userId);

        T obj = RpcUtil.getObjectParam(exchange, tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }
}
