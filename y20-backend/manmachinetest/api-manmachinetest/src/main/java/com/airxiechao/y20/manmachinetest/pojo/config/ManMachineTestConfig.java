package com.airxiechao.y20.manmachinetest.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("man-machine-test.yml")
public class ManMachineTestConfig {

    private String name;
    private int port;
    private String questionTokenEncryptKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQuestionTokenEncryptKey() {
        return questionTokenEncryptKey;
    }

    public void setQuestionTokenEncryptKey(String questionTokenEncryptKey) {
        this.questionTokenEncryptKey = questionTokenEncryptKey;
    }
}
