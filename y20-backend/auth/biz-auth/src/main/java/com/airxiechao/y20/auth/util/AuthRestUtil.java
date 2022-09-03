package com.airxiechao.y20.auth.util;

import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.common.core.biz.Biz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.undertow.server.HttpServerExchange;

public class AuthRestUtil {

    private static IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);

    public static <T> T queryDataWithAccessPrincipal(HttpServerExchange exchange, Class<T> tClass) throws Exception {
        String accessToken = RestUtil.getAuthToken(exchange);
        AccessPrincipal accessPrincipal = accessTokenBiz.extractAccessPrincipal(accessToken);;
        Long userId = accessPrincipal.getUserId();

        JSONObject jsonObject = RestUtil.queryJsonData(exchange);
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("userId", userId);

        T obj = jsonObject.toJavaObject(tClass);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }

    public static <T> T rawJsonDataWithAccessPrincipal(HttpServerExchange exchange, Class<T> cls) throws Exception {
        String accessToken = RestUtil.getAuthToken(exchange);
        AccessPrincipal accessPrincipal = accessTokenBiz.extractAccessPrincipal(accessToken);
        Long userId = accessPrincipal.getUserId();

        String jsonString = RestUtil.rawStringData(exchange);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if(null == jsonObject){
            jsonObject = new JSONObject();
        }
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("userId", userId);

        T obj = jsonObject.toJavaObject(cls);
        ClsUtil.checkRequiredField(obj);
        return obj;
    }
}
