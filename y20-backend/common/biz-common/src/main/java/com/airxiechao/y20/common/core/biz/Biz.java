package com.airxiechao.y20.common.core.biz;

import com.airxiechao.axcboot.core.biz.BizReg;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Biz extends BizReg {

    private static final Logger logger = LoggerFactory.getLogger(Biz.class);

    private static final Biz instance = new Biz();
    public static Biz getInstance() {
        return instance;
    }

    public static <T> T get(Class<T> interfaceCls) {
        return getBizImplProxy(() -> instance, interfaceCls);
    }

    private Biz() {
        super(Meta.getProjectPackageName());
        this.registerProcessIfExists();
    }
}
