package com.airxiechao.y20.common.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("variable-common.yml")
public class VariableCommonConfig {
    private String secretVariableEncryptKey;

    public String getSecretVariableEncryptKey() {
        return secretVariableEncryptKey;
    }

    public void setSecretVariableEncryptKey(String secretVariableEncryptKey) {
        this.secretVariableEncryptKey = secretVariableEncryptKey;
    }
}
