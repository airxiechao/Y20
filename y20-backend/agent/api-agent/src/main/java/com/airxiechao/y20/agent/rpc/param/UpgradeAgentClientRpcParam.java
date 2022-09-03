package com.airxiechao.y20.agent.rpc.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpgradeAgentClientRpcParam {
    @Required private String downloadUrl;
    @Required private String downloadUpgraderUrl;

    public UpgradeAgentClientRpcParam() {
    }

    public UpgradeAgentClientRpcParam(String downloadUrl, String downloadUpgraderUrl) {
        this.downloadUrl = downloadUrl;
        this.downloadUpgraderUrl = downloadUpgraderUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUpgraderUrl() {
        return downloadUpgraderUrl;
    }

    public void setDownloadUpgraderUrl(String downloadUpgraderUrl) {
        this.downloadUpgraderUrl = downloadUpgraderUrl;
    }
}
