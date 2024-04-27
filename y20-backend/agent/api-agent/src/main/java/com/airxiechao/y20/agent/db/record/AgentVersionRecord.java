package com.airxiechao.y20.agent.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

import java.util.Date;

@Table("agent_version")
@Index(fields = {"version"}, unique = true)
public class AgentVersionRecord {
    private Long id;
    @Column(length = 50) private String version;
    @Column(length = 500) private String downloadWinUrl;
    @Column(length = 500) private String downloadLinuxUrl;
    @Column(length = 500) private String downloadWinUpgraderUrl;
    @Column(length = 500) private String downloadLinuxUpgraderUrl;
    private Date releaseTime;

    public AgentVersionRecord() {
    }

    public AgentVersionRecord(String version, String downloadWinUrl, String downloadLinuxUrl, String downloadWinUpgraderUrl, String downloadLinuxUpgraderUrl, Date releaseTime) {
        this.version = version;
        this.downloadWinUrl = downloadWinUrl;
        this.downloadLinuxUrl = downloadLinuxUrl;
        this.downloadWinUpgraderUrl = downloadWinUpgraderUrl;
        this.downloadLinuxUpgraderUrl = downloadLinuxUpgraderUrl;
        this.releaseTime = releaseTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadWinUrl() {
        return downloadWinUrl;
    }

    public void setDownloadWinUrl(String downloadWinUrl) {
        this.downloadWinUrl = downloadWinUrl;
    }

    public String getDownloadLinuxUrl() {
        return downloadLinuxUrl;
    }

    public void setDownloadLinuxUrl(String downloadLinuxUrl) {
        this.downloadLinuxUrl = downloadLinuxUrl;
    }

    public String getDownloadWinUpgraderUrl() {
        return downloadWinUpgraderUrl;
    }

    public void setDownloadWinUpgraderUrl(String downloadWinUpgraderUrl) {
        this.downloadWinUpgraderUrl = downloadWinUpgraderUrl;
    }

    public String getDownloadLinuxUpgraderUrl() {
        return downloadLinuxUpgraderUrl;
    }

    public void setDownloadLinuxUpgraderUrl(String downloadLinuxUpgraderUrl) {
        this.downloadLinuxUpgraderUrl = downloadLinuxUpgraderUrl;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }
}
