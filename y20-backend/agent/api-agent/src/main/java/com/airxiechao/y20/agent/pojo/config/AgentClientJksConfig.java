package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-client-jks.yml")
public class AgentClientJksConfig {

    private String clientJks;
    private String clientJksPassword;
    private String trustJks;
    private String trustJksPassword;

    public String getClientJks() {
        return clientJks;
    }

    public void setClientJks(String clientJks) {
        this.clientJks = clientJks;
    }

    public String getClientJksPassword() {
        return clientJksPassword;
    }

    public void setClientJksPassword(String clientJksPassword) {
        this.clientJksPassword = clientJksPassword;
    }

    public String getTrustJks() {
        return trustJks;
    }

    public void setTrustJks(String trustJks) {
        this.trustJks = trustJks;
    }

    public String getTrustJksPassword() {
        return trustJksPassword;
    }

    public void setTrustJksPassword(String trustJksPassword) {
        this.trustJksPassword = trustJksPassword;
    }
}

