package com.airxiechao.y20.common.core.global;

import com.airxiechao.axcboot.util.lang.ImplReg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Global extends ImplReg {
    private static final Global instance = new Global();
    public static Global getInstance() {
        return instance;
    }

    private Map<String, Object> injectMap = new ConcurrentHashMap<>();

    private Global(){}

    public static <T> T get(Class<T> interfaceCls){
        return getImplProxy(()-> instance, interfaceCls);
    }

    public void provide(String key, Object value){
        injectMap.put(key, value);
    }

    public <T> T inject(String key){
        return (T)injectMap.get(key);
    }
}
