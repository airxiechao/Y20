package com.airxiechao.y20.sms.biz.process.sender;

public interface ISmsSender {

    void sendVerificationCode(String mobile, String code) throws Exception;
    void sendMonitorAlert(String mobile, String monitorName) throws Exception;
}
