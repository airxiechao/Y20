package com.airxiechao.y20.common.pojo.constant.meta;

public class Meta {
    public static String getProjectPackageName(){
        String pkg = Meta.class.getPackageName();
        String[] tokens = pkg.split("\\.");
        return String.format("%s.%s.%s", tokens[0], tokens[1], tokens[2]);
    };

    public static String getModulePackageName(Class cls){
        String pkg = cls.getPackageName();
        String[] tokens = pkg.split("\\.");
        return String.format("%s.%s.%s.%s", tokens[0], tokens[1], tokens[2], tokens[3]);
    };
}
