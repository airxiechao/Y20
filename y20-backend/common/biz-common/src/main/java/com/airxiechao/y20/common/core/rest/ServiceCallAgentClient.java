package com.airxiechao.y20.common.core.rest;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ProxyUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.rest.param.CallAgentParam;
import com.airxiechao.y20.agent.rest.api.IServiceAgentServerRest;
import com.airxiechao.y20.agent.rpc.api.client.ILocalAgentClientRpc;
import com.airxiechao.y20.agent.rpc.param.ReadAgentClientConfigRpcParam;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ExtractAccessPrincipalParam;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.agent.AgentUtil;
import com.airxiechao.y20.common.core.cache.AgentServiceTagCache;
import com.airxiechao.y20.common.pipeline.annotation.ValidateAgent;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Date;

public class ServiceCallAgentClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCallAgentClient.class);

    public static <T> T get(Class<T> cls, String clientId){
        String interfaceName = cls.getName();
        return ProxyUtil.buildProxy(cls, (proxy, method, args) -> {
            String methodName = method.getName();
            Object methodParam = args[0];
            String serviceTag = getServiceTag(clientId);

            ValidateAgent validateAgent = method.getAnnotation(ValidateAgent.class);
            if(null != validateAgent){
                // validate agent
                Response<String> readConfigResp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).readConfig(
                        new ReadAgentClientConfigRpcParam());
                if(!readConfigResp.isSuccess()){
                    logger.error("read agent client config error: {}", readConfigResp.getMessage());
                    throw new Exception("read agent client config error");
                }

                String config = readConfigResp.getData();
                String accessToken = AgentUtil.extractAccessTokenFromConfig(config);
                if(StringUtil.isBlank(accessToken)){
                    throw new Exception("agent client not config access token");
                }

                ValidateAccessTokenParam validationParam = new ValidateAccessTokenParam(
                        accessToken, EnumAccessScope.AGENT, null, null);
                Response principalResp = ServiceRestClient.get(IServiceAuthRest.class).validateAccessToken(validationParam);

                if(!principalResp.isSuccess()){
                    logger.error("validate agent client access token fail: {}", principalResp.getMessage());
                    throw new Exception("validate agent client access token fail");
                }
            }

            Response resp = ServiceTaggedRestClient.get(IServiceAgentServerRest.class, serviceTag).callAgent(
                    new CallAgentParam(clientId, interfaceName, methodName, methodParam));

            Type retType = method.getGenericReturnType();
            JSONObject jsonResp = (JSONObject) JSON.toJSON(resp);
            Object ret = jsonResp.toJavaObject(retType);
            return ret;
        });
    }

    private static String getServiceTag(String clientId){
        return AgentServiceTagCache.getInstance().getAgentServiceTag(clientId);
    }
}
