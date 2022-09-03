package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumArtifactFileDestDir;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;

public class RemotePluginTencentCosCmdStepRunParam {

    @StepTypeParam(
            displayOrder = 1,
            displayName = "是否需要安装配置",
            hint = "安装配置 COSCMD 工具",
            type = EnumPipelineParamType.SELECT,
            options = {
                    @StepTypeSelectOption(value = "true", label = "是"),
                    @StepTypeSelectOption(value = "false", label = "否"),
            },
            defaultValue = "true"
    )
    @Required
    private Boolean flagInstall;

    @StepTypeParam(
            displayOrder = 2,
            displayName = "密钥 ID",
            hint = "如果需要安装配置，请填写（可以引用环境变量）。否则，不填",
            type = EnumPipelineParamType.INPUT)
    @Required(conditionalOnRequiredTrue = "flagInstall")
    private String secretId;

    @StepTypeParam(
            displayOrder = 3,
            displayName = "密钥 Key",
            hint = "如果需要安装配置，请填写（可以引用环境变量）。否则，不填",
            type = EnumPipelineParamType.INPUT)
    @Required(conditionalOnRequiredTrue = "flagInstall")
    private String secretKey;

    @StepTypeParam(
            displayOrder = 4,
            displayName = "存储桶名称",
            hint = "如果需要安装配置，请填写（可以引用环境变量）。否则，不填",
            type = EnumPipelineParamType.INPUT)
    @Required(conditionalOnRequiredTrue = "flagInstall")
    private String bucket;

    @StepTypeParam(
            displayOrder = 5,
            displayName = "存储桶所在地域",
            hint = "如果需要安装配置，请填写（可以引用环境变量）。否则，不填",
            type = EnumPipelineParamType.INPUT)
    @Required(conditionalOnRequiredTrue = "flagInstall")
    private String region;

    @StepTypeParam(
            displayOrder = 6,
            displayName = "脚本",
            hint = "执行 COSCMD 的命令行脚本",
            type = EnumPipelineParamType.TEXT)
    @Required
    private String script;

    public RemotePluginTencentCosCmdStepRunParam() {
    }

    public RemotePluginTencentCosCmdStepRunParam(Boolean flagInstall, String secretId, String secretKey, String bucket, String region, String script) {
        this.flagInstall = flagInstall;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.bucket = bucket;
        this.region = region;
        this.script = script;
    }

    public Boolean getFlagInstall() {
        return flagInstall;
    }

    public void setFlagInstall(Boolean flagInstall) {
        this.flagInstall = flagInstall;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
