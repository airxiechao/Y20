package com.airxiechao.y20.auth.pojo;

import java.util.HashMap;
import java.util.Map;

public class AccessPolicy {

    private boolean permitAll = false;
    private Map<String, Integer> permitItemModeMap = new HashMap<>();

    public boolean isPermitAll() {
        return permitAll;
    }

    public void setPermitAll(boolean permitAll) {
        this.permitAll = permitAll;
    }

    public Map<String, Integer> getPermitItemModeMap() {
        return permitItemModeMap;
    }

    public void setPermitItemModeMap(Map<String, Integer> permitItemModeMap) {
        this.permitItemModeMap = permitItemModeMap;
    }

    public boolean permitItemMode(String targetItem, int targetMode){
        Integer mode = this.permitItemModeMap.get(targetItem);
        if(null == mode){
            return false;
        }

        return mode > targetMode;
    }
}
