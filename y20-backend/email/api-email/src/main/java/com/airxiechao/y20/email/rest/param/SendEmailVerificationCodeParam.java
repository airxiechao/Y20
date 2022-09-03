package com.airxiechao.y20.email.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class SendEmailVerificationCodeParam {

    @Required private String manMachineTestQuestionToken;
    @Required private String manMachineTestAnswer;
    @Required private Long userId;
    private String email;

    public String getManMachineTestQuestionToken() {
        return manMachineTestQuestionToken;
    }

    public void setManMachineTestQuestionToken(String manMachineTestQuestionToken) {
        this.manMachineTestQuestionToken = manMachineTestQuestionToken;
    }

    public String getManMachineTestAnswer() {
        return manMachineTestAnswer;
    }

    public void setManMachineTestAnswer(String manMachineTestAnswer) {
        this.manMachineTestAnswer = manMachineTestAnswer;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
