package com.airxiechao.y20.sms.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class SendSmsVerificationCodeParam {

    @Required private String manMachineTestQuestionToken;
    @Required private String manMachineTestAnswer;
    private Long userId;
    private String mobile;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
