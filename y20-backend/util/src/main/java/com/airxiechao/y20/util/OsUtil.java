package com.airxiechao.y20.util;

import org.apache.commons.lang.SystemUtils;

public class OsUtil {

    public static final String OS_WINDOWS = "WINDOWS";
    public static final String OS_LINUX = "LINUX";
    public static final String OS_MAC = "MAC";
    public static final String OS_UNKNOWN = "UNKNOWN";

    public static String getOs(){
        if(SystemUtils.IS_OS_WINDOWS){
            return OS_WINDOWS;
        }else if(SystemUtils.IS_OS_LINUX){
            return OS_LINUX;
        }else if(SystemUtils.IS_OS_MAC){
            return OS_MAC;
        }else{
            return OS_UNKNOWN;
        }
    }
}
