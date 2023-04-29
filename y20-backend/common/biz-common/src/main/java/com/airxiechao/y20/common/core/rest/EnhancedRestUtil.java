package com.airxiechao.y20.common.core.rest;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;
import com.airxiechao.y20.auth.rest.api.IServiceTeamRest;
import com.airxiechao.y20.auth.rest.param.ExtractAccessPrincipalParam;
import com.airxiechao.y20.auth.rest.param.GetAccountParam;
import com.airxiechao.y20.auth.rest.param.ServiceGetJoinedTeamParam;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class EnhancedRestUtil {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRestUtil.class);

    public static boolean validateAccessToken(String accessToken, String scope, String item, Integer mode){
        ValidateAccessTokenParam param = new ValidateAccessTokenParam(accessToken, scope, item, mode);
        Response resp = ServiceRestClient.get(IServiceAuthRest.class).validateAccessToken(param);

        return resp.isSuccess();
    }

    public static Object resolveParam(HttpServerExchange exchange, Method method, boolean switchToTeamUserId) throws Exception {
        Get get = AnnotationUtil.getMethodAnnotation(method, Get.class);
        if(null != get){
            if(get.value().startsWith("/service/")){
                return RestUtil.queryData(exchange, method.getParameterTypes()[0]);
            }else{
                return EnhancedRestUtil.queryDataWithHeader(exchange, method.getParameterTypes()[0], switchToTeamUserId);
            }
        }

        Post post = AnnotationUtil.getMethodAnnotation(method, Post.class);
        if(null != post) {
            if(post.value().startsWith("/service/")) {
                return RestUtil.rawJsonData(exchange, method.getParameterTypes()[0]);
            }else{
                return EnhancedRestUtil.rawJsonDataWithHeader(exchange, method.getParameterTypes()[0], switchToTeamUserId);
            }
        }

        throw new Exception("resolve param error: http method not support");
    }

    public static JoinedTeamVo getJoinedTeam(Long memberUserId, Long joinedTeamId) throws Exception {
        Response<JoinedTeamVo> resp = ServiceRestClient.get(IServiceTeamRest.class).getJoinedTeam(
                new ServiceGetJoinedTeamParam(memberUserId, joinedTeamId));
        if(!resp.isSuccess()){
            logger.error("get joined team error: {}", resp.getMessage());
            throw new Exception(resp.getMessage());
        }

        return resp.getData();
    }

    public static void switchToTeamOwnerUserId(String teamId, JSONObject jsonObject) throws Exception {
        Long userId = jsonObject.getLong("userId");
        if(!StringUtil.isBlank(teamId)){
            JoinedTeamVo joinedTeamVo = getJoinedTeam(userId, Long.valueOf(teamId));
            jsonObject.put("userId", joinedTeamVo.getUserId());
        }
    }

    public static AccessPrincipal extractAccessPrincipal(HttpServerExchange exchange) throws Exception {
        String accessToken = RestUtil.getAuthToken(exchange);
        ExtractAccessPrincipalParam param = new ExtractAccessPrincipalParam(accessToken);
        Response<AccessPrincipal> resp = ServiceRestClient.get(IServiceAuthRest.class).extractAccessPrincipal(param);
        if(!resp.isSuccess()){
            logger.error("extract rest access principal error: {}", resp.getMessage());
            throw new Exception(resp.getMessage());
        }
        AccessPrincipal accessPrincipal = resp.getData();
        return accessPrincipal;
    }

    public static AccessPrincipal extractWsAccessPrincipal(WebSocketHttpExchange exchange) throws Exception {
        String accessToken = RestUtil.getWsAuthToken(exchange);
        ExtractAccessPrincipalParam param = new ExtractAccessPrincipalParam(accessToken);
        Response<AccessPrincipal> resp = ServiceRestClient.get(IServiceAuthRest.class).extractAccessPrincipal(param);
        if(!resp.isSuccess()){
            logger.error("extract rest access principal error: {}", resp.getMessage());
            throw new Exception(resp.getMessage());
        }
        AccessPrincipal accessPrincipal = resp.getData();
        return accessPrincipal;
    }

    public static <T> T rawJsonDataWithHeader(HttpServerExchange exchange, Class<T> tClass, boolean switchToTeamUserId) throws Exception {
        // get user id
        AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        String jsonString = RestUtil.rawStringData(exchange);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if(null == jsonObject){
            jsonObject = new JSONObject();
        }
        jsonObject.put("userId", userId);

        if(switchToTeamUserId) {
            // switch to team owner's userId
            String teamId = RestUtil.getHeader(exchange, "teamId");
            switchToTeamOwnerUserId(teamId, jsonObject);
        }

        String projectId = RestUtil.getHeader(exchange, "projectId");
        if(!jsonObject.containsKey("projectId") && !StringUtil.isBlank(projectId)){
            jsonObject.put("projectId", projectId);
        }

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }

    public static <T> T queryDataWithHeader(HttpServerExchange exchange, Class<T> tClass, boolean switchToTeamUserId) throws Exception {
        // get user id
        AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        JSONObject jsonObject = RestUtil.queryJsonData(exchange);
        jsonObject.put("userId", userId);

        if(switchToTeamUserId) {
            // switch to team owner's userId
            String teamId = RestUtil.getHeaderOrCookieOrParam(exchange, "teamId");
            switchToTeamOwnerUserId(teamId, jsonObject);
        }

        String projectId = RestUtil.getHeader(exchange, "projectId");
        if(!jsonObject.containsKey("projectId") && !StringUtil.isBlank(projectId)){
            jsonObject.put("projectId", projectId);
        }

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }

    public static <T> T formDataWithHeader(HttpServerExchange exchange, Class<T> tClass, boolean switchToTeamUserId) throws Exception {
        // get user id
        AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        Map<String, Object> formData = RestUtil.allFormData(exchange);
        JSONObject jsonObject = new JSONObject(formData);
        jsonObject.put("userId", userId);

        if(switchToTeamUserId) {
            // switch to team owner's userId
            String teamId = RestUtil.getHeader(exchange, "teamId");
            switchToTeamOwnerUserId(teamId, jsonObject);
        }

        // get project id
        String projectId = RestUtil.getHeader(exchange, "projectId");
        if(!jsonObject.containsKey("projectId") && !StringUtil.isBlank(projectId)){
            jsonObject.put("projectId", projectId);
        }

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }

    public static <T> T multiPartFormDataWithHeader(HttpServerExchange exchange, Class<T> tClass, boolean switchToTeamUserId) throws Exception {
        // get user id
        AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        Map<String, Object> formData = RestUtil.allMultiPartFormData(exchange);
        JSONObject jsonObject = new JSONObject(formData);
        jsonObject.put("userId", userId);

        if(switchToTeamUserId) {
            // switch to team owner's userId
            String teamId = RestUtil.getHeader(exchange, "teamId");
            switchToTeamOwnerUserId(teamId, jsonObject);
        }

        // get project id
        String projectId = RestUtil.getHeader(exchange, "projectId");
        if(!jsonObject.containsKey("projectId") && !StringUtil.isBlank(projectId)){
            jsonObject.put("projectId", projectId);
        }

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }

    public static <T> T queryWsDataWithHeader(WebSocketHttpExchange exchange, Class<T> tClass, boolean switchToTeamUserId) throws Exception {
        // get user id
        AccessPrincipal accessPrincipal = EnhancedRestUtil.extractWsAccessPrincipal(exchange);
        Long userId = accessPrincipal.getUserId();

        JSONObject jsonObject = RestUtil.queryWsJsonData(exchange);
        jsonObject.put("userId", userId);

        if(switchToTeamUserId) {
            // switch to team owner's userId
            String teamId = RestUtil.getWsHeaderOrParam(exchange, "teamId");
            switchToTeamOwnerUserId(teamId, jsonObject);
        }

        String projectId = RestUtil.getWsHeader(exchange, "projectId");
        if(!jsonObject.containsKey("projectId") && !StringUtil.isBlank(projectId)){
            jsonObject.put("projectId", projectId);
        }

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }
}
