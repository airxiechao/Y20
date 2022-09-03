package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class LoginByUsernameParam {

    @Required private String username;
    @Required private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
