package com.airxiechao.y20.manmachinetest.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CheckManMachineQuestionParam {

    @Required private String token;
    @Required private String answer;

    public CheckManMachineQuestionParam(String token, String answer) {
        this.token = token;
        this.answer = answer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
