package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-server-jks.yml")
public class AgentServerJksConfig {

    private String serverJks;
    private String serverJksPassword;
    private String trustJks;
    private String trustJksPassword;

    public String getServerJks() {
        return serverJks;
    }

    public void setServerJks(String serverJks) {
        this.serverJks = serverJks;
    }

    public String getServerJksPassword() {
        return serverJksPassword;
    }

    public void setServerJksPassword(String serverJksPassword) {
        this.serverJksPassword = serverJksPassword;
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
