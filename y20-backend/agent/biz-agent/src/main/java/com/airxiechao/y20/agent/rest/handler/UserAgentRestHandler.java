package com.airxiechao.y20.agent.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;
import com.airxiechao.y20.agent.pojo.Agent;
import com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus;
import com.airxiechao.y20.agent.pojo.vo.AgentDetailVo;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentServerRest;
import com.airxiechao.y20.agent.rest.api.IUserAgentRest;
import com.airxiechao.y20.agent.biz.api.IAgentBiz;
import com.airxiechao.y20.agent.rest.param.*;
import com.airxiechao.y20.agent.rpc.api.client.ILocalAgentClientRpc;
import com.airxiechao.y20.agent.rpc.param.*;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.pojo.vo.AccessTokenVo;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ServiceGetAccessTokenParam;
import com.airxiechao.y20.common.agent.AgentUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceAgentServerRestClient;
import com.airxiechao.y20.common.core.rest.ServiceCallAgentClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceGetPipelineRunRunningAgentParam;
import com.airxiechao.y20.util.OsUtil;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAgentRestHandler implements IUserAgentRest {

    private static final Logger logger = LoggerFactory.getLogger(UserAgentRestHandler.class);
    private IAgentBiz agentBiz = Biz.get(IAgentBiz.class);

    @Override
    public Response listAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListAgentParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<AgentVo> list = agentBiz.list(
                param.getUserId(),
                param.getAgentId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(r -> {
            Agent agent = r.toPojo();

            boolean active = false;

            if(!EnumAgentStatus.STATUS_OFFLINE.equals(agent.getStatus())) {
                try {
                    String clientId = agent.getClientId();

                    // check agent real status
                    Response activeResp = ServiceAgentServerRestClient.get(IServiceAgentServerRest.class, clientId).isAgentActive(
                            new IsAgentActiveParam(agent.getClientId()));
                    active = activeResp.isSuccess();
                } catch (Exception e) {
                    logger.error("get agent client detail error", e);
                }
            }

            return new AgentVo(agent, active);

        }).collect(Collectors.toList());

        long count = agentBiz.count(param.getUserId(), param.getAgentId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response listDetailAgent(Object exc) {

        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListAgentParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // list running agents
        Set<String> runningAgents = new HashSet<>();
        try {
            Response<Set<String>> runningAgentResp = ServiceRestClient.get(IServicePipelineRest.class).listPipelineRunRunningAgent(
                    new ServiceGetPipelineRunRunningAgentParam(param.getUserId()));
            if (!runningAgentResp.isSuccess()) {
                throw new Exception(runningAgentResp.getMessage());
            }

            runningAgents = runningAgentResp.getData();
        }catch (Exception e){
            logger.error("list pipeline run running agent error", e);
        }

        Set<String> runningAgentSet = runningAgents;
        List<AgentDetailVo> list = agentBiz.list(
                param.getUserId(),
                param.getAgentId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(r -> {
            Agent agent = r.toPojo();

            boolean active = false;
            boolean running = false;
            String accessTokenName = null;
            Date accessTokenEndTime = null;

            if(!EnumAgentStatus.STATUS_OFFLINE.equals(agent.getStatus())) {
                try {
                    String clientId = agent.getClientId();

                    // check agent real status
                    Response activeResp = ServiceAgentServerRestClient.get(IServiceAgentServerRest.class, clientId).isAgentActive(
                            new IsAgentActiveParam(agent.getClientId()));
                    active = activeResp.isSuccess();

                    if (active) {
                        // check running
                        running = runningAgentSet.contains(agent.getAgentId());

                        // get accessToken name and endTime
                        Response<String> readConfigResp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).readConfig(
                                new ReadAgentClientConfigRpcParam());
                        if (!readConfigResp.isSuccess()) {
                            logger.error("read agent client config error: {}", readConfigResp.getMessage());
                            throw new Exception("read agent client config error");
                        }

                        String config = readConfigResp.getData();
                        String accessToken = AgentUtil.extractAccessTokenFromConfig(config);
                        if (StringUtil.isBlank(accessToken)) {
                            throw new Exception("agent client not config access token");
                        }

                        Response<AccessTokenRecord> accessTokenResp = ServiceRestClient.get(IServiceAuthRest.class).getAccessToken(
                                new ServiceGetAccessTokenParam(accessToken));
                        if (!accessTokenResp.isSuccess()) {
                            throw new Exception("no access token record");
                        }

                        AccessTokenRecord accessTokenRecord = accessTokenResp.getData();
                        accessTokenName = accessTokenRecord.getName();
                        accessTokenEndTime = accessTokenRecord.getEndTime();
                    }
                } catch (Exception e) {
                    logger.error("get agent client detail error", e);
                }
            }

            return new AgentDetailVo(agent, active, running, accessTokenName, accessTokenEndTime);

        }).collect(Collectors.toList());

        long count = agentBiz.count(param.getUserId(), param.getAgentId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response getAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetAgentParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        return new Response().data(agentRecord.toPojo());
    }

    @Override
    public Response createAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateAgentParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, CreateAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.create(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response getLatestAgentVersion(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        AgentVersionRecord agentVersionRecord = agentBiz.getLatestVersion();
        if(null == agentVersionRecord){
            return new Response().error("no agent version");
        }

        return new Response().data(agentVersionRecord);
    }

    @Override
    public Response upgradeAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpgradeAgentParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpgradeAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        String clientId = agentRecord.getClientId();
        if(StringUtil.isBlank(clientId)){
            return new Response().error("agent not active");
        }

        AgentVersionRecord latestVersion = agentBiz.getLatestVersion();
        if(null == latestVersion){
            return new Response().error("no agent version");
        }

        // check agent version against latest version
        if(latestVersion.getVersion().equals(agentRecord.getVersion())){
            //return new Response().error("agent version is already latest");
        }

        String agentOs = agentRecord.getOs();
        UpgradeAgentClientRpcParam upgradeAgentRpcParam = null;
        switch (agentOs){
            case OsUtil.OS_LINUX:
                upgradeAgentRpcParam = new UpgradeAgentClientRpcParam(latestVersion.getDownloadLinuxUrl(), latestVersion.getDownloadLinuxUpgraderUrl());
                break;
            case OsUtil.OS_WINDOWS:
                upgradeAgentRpcParam = new UpgradeAgentClientRpcParam(latestVersion.getDownloadWinUrl(), latestVersion.getDownloadWinUpgraderUrl());
                break;
        }
        if(null == upgradeAgentRpcParam){
            return new Response().error("agent's os is not supported");
        }

        try {
            agentBiz.updateUpgrading(agentRecord.getUserId(), agentRecord.getAgentId(), true);

            Response resp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).upgrade(upgradeAgentRpcParam);
            if(!resp.isSuccess()){
                throw new Exception(resp.getMessage());
            }

            return new Response();
        } catch (Exception e) {
            agentBiz.updateUpgrading(agentRecord.getUserId(), agentRecord.getAgentId(), false);

            logger.error("upgrade agent client error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response uninstallAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UninstallAgentParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UninstallAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        try{
            String clientId = agentRecord.getClientId();
            if(StringUtil.isBlank(clientId)){
                agentBiz.deleteByUserIdAndAgentId(agentRecord.getUserId(), agentRecord.getAgentId());
                return new Response();
            }

            Response activeResp = ServiceAgentServerRestClient.get(IServiceAgentServerRest.class, clientId).isAgentActive(
                    new IsAgentActiveParam(clientId));
            if(!activeResp.isSuccess()){
                agentBiz.deleteByUserIdAndAgentId(agentRecord.getUserId(), agentRecord.getAgentId());
                return new Response();
            }

            Response uninstallResp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).uninstall(
                    new UninstallAgentClientRpcParam());
            if(!uninstallResp.isSuccess()){
                throw new Exception(uninstallResp.getMessage());
            }

            agentBiz.deleteByUserIdAndAgentId(agentRecord.getUserId(), agentRecord.getAgentId());

            return new Response();
        }catch (Exception e){
            logger.error("uninstall agent client error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response restartAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        RestartAgentParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, RestartAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        String clientId = agentRecord.getClientId();
        if(StringUtil.isBlank(clientId)){
            return new Response().error("agent not active");
        }

        try {
            agentBiz.updateRestarting(agentRecord.getUserId(), agentRecord.getAgentId(), true);

            Response resp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).restart(
                    new RestartAgentClientRpcParam());
            if(!resp.isSuccess()){
                throw new Exception(resp.getMessage());
            }

            return new Response();
        } catch (Exception e) {
            agentBiz.updateRestarting(agentRecord.getUserId(), agentRecord.getAgentId(), false);

            logger.error("restart agent client error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response readAgentConfig(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ReadAgentConfigParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ReadAgentConfigParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        String clientId = agentRecord.getClientId();
        if(StringUtil.isBlank(clientId)){
            return new Response().error("agent not active");
        }

        try {
            Response resp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).readConfig(
                    new ReadAgentClientConfigRpcParam());
            if(!resp.isSuccess()){
                throw new Exception(resp.getMessage());
            }

            return new Response().data(resp.getData());
        } catch (Exception e) {
            logger.error("read agent client config error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response getAgentAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetAgentAccessTokenParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetAgentAccessTokenParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        try {
            Response<AccessTokenRecord> accessTokenResp = ServiceRestClient.get(IServiceAuthRest.class).getAccessToken(
                    new ServiceGetAccessTokenParam(param.getAgentAccessToken()));
            if (!accessTokenResp.isSuccess()) {
                throw new Exception("no access token record");
            }

            AccessTokenRecord accessTokenRecord = accessTokenResp.getData();
            AccessTokenVo vo = new AccessTokenVo(accessTokenRecord);
            return new Response().data(vo);
        } catch (Exception e) {
            logger.error("get agent access token error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response saveAgentConfig(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        SaveAgentConfigParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, SaveAgentConfigParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        String clientId = agentRecord.getClientId();
        if(StringUtil.isBlank(clientId)){
            return new Response().error("agent not active");
        }

        try {
            Response resp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).saveConfig(
                    new SaveAgentClientConfigRpcParam(param.getConfig()));
            if(!resp.isSuccess()){
                throw new Exception(resp.getMessage());
            }

            return new Response();
        } catch (Exception e) {
            logger.error("save agent client config error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response cleanAgentConfig(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CleanAgentParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CleanAgentParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }

        String clientId = agentRecord.getClientId();
        if(StringUtil.isBlank(clientId)){
            return new Response().error("agent not active");
        }

        try {
            Response resp = ServiceCallAgentClient.get(ILocalAgentClientRpc.class, clientId).clean(
                    new CleanAgentClientRpcParam());
            if(!resp.isSuccess()){
                throw new Exception(resp.getMessage());
            }

            return new Response();
        } catch (Exception e) {
            logger.error("clean agent client error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response generateAgentJoinScript(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GenerateAgentJoinScriptParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, GenerateAgentJoinScriptParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String osType = param.getOsType();
        String agentId = param.getAgentId();
        String accessToken = param.getAccessToken();
        String serverHost = param.getServerHost();
        Integer serverRpcPort = param.getServerRpcPort();
        Boolean serverRestUseSsl = param.getServerRestUseSsl();
        String dataDir = param.getDataDir();

        AgentVersionRecord latestVersion = agentBiz.getLatestVersion();
        if(null == latestVersion){
            return new Response().error("no agent version");
        }

        String script = null;
        switch (osType){
            case OsUtil.OS_WINDOWS:
                script = generateWindowsAgentJoinScript(
                        agentId, accessToken, serverHost, serverRpcPort, serverRestUseSsl,
                        dataDir, latestVersion.getDownloadWinUrl()
                );
                break;
            case OsUtil.OS_LINUX:
                script = generateLinuxAgentJoinScript(
                        agentId, accessToken, serverHost, serverRpcPort, serverRestUseSsl,
                        dataDir, latestVersion.getDownloadLinuxUrl()
                );
                break;
        }

        if(StringUtil.isBlank(script)){
            return new Response().error("os type not support");
        }

        return new Response().data(script);
    }

    private String generateLinuxAgentJoinScript(
            String agentId, String accessToken, String serverHost, Integer serverRpcPort, Boolean serverRestUseSsl,
            String dataDir, String downloadUrl
    ){
        return String.format("sudo bash << EOF\n" +
            "# download\n" +
            "echo \"1. download...\"\n" +
            "curl -kfL \"%s\" -o y20-agent-client-linux.zip\n" +
            "rm -rf y20-agent-client\n" +
            "unzip y20-agent-client-linux.zip\n" +
            "rm y20-agent-client-linux.zip\n" +
            "cd y20-agent-client\n" +
            "\n" +
            "# config\n" +
            "echo \"2. config...\"\n" +
            "cd conf\n" +
            "sed -i s/agentId:.*/'agentId: %s'/ agent-client.yml\n" +
            "sed -i s/accessToken:.*/'accessToken: %s'/ agent-client.yml\n" +
            "sed -i s/serverHost:.*/'serverHost: %s'/ agent-client.yml\n" +
            "sed -i s/serverRpcPort:.*/'serverRpcPort: %d'/ agent-client.yml\n" +
            "sed -i s/serverRestUseSsl:.*/'serverRestUseSsl: %b'/ agent-client.yml\n" +
            "sed -i 's|dataDir:.*|dataDir: %s|' agent-client.yml\n" +
            "\n" +
            "# install service and start\n" +
            "echo \"3. install...\"\n" +
            "cd -\n" +
            "chmod +x *.sh\n" +
            "./install-y20-agent-client-service.sh\n" +
            "EOF\n", downloadUrl, agentId, accessToken, serverHost, serverRpcPort, serverRestUseSsl, dataDir);
    }

    private String generateWindowsAgentJoinScript(
            String agentId, String accessToken, String serverHost, Integer serverRpcPort, Boolean serverRestUseSsl,
            String dataDir, String downloadUrl
    ){
        return String.format("$curDir = Get-Location; Start-Process powershell -Verb runAs -Wait -ArgumentList @(\"cd $curDir;\"+'\n" +
                "# download\n"+
                "echo \\\"1. download...\\\"\n" +
                "add-type @\\\"\n" +
                "  using System.Net;\n" +
                "  using System.Security.Cryptography.X509Certificates;\n" +
                "  public class TrustAllCertsPolicy : ICertificatePolicy {\n" +
                "    public bool CheckValidationResult(ServicePoint srvPoint, X509Certificate certificate, WebRequest request, int certificateProblem) {\n" +
                "        return true;\n" +
                "      }\n" +
                "    }\n" +
                "\\\"@\n" +
                "[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy\n" +
                "Invoke-WebRequest -Uri \\\"%s\\\" -OutFile y20-agent-client-win.zip;\n"+
                "Remove-Item y20-agent-client -Recurse -Force -ErrorAction Ignore;\n"+
                "Expand-Archive -Path y20-agent-client-win.zip -DestinationPath . -Force;\n"+
                "Remove-Item y20-agent-client-win.zip;\n"+
                "cd y20-agent-client;\n"+
                "\n"+
                "# config\n"+
                "echo \\\"2. config...\\\"\n" +
                "cd conf;\n"+
                "$conf = get-content agent-client.yml;\n"+
                "$conf = $conf -replace \\\"agentId:.*\\\", \\\"agentId: %s\\\";\n"+
                "$conf = $conf -replace \\\"accessToken:.*\\\", \\\"accessToken: %s\\\";\n"+
                "$conf = $conf -replace \\\"serverHost:.*\\\", \\\"serverHost: %s\\\";\n"+
                "$conf = $conf -replace \\\"serverRpcPort:.*\\\", \\\"serverRpcPort: %d\\\";\n"+
                "$conf = $conf -replace \\\"serverRestUseSsl:.*\\\", \\\"serverRestUseSsl: %b\\\";\n"+
                "$conf = $conf -replace \\\"dataDir:.*\\\", \\\"dataDir: %s\\\";\n"+
                "$conf | set-content -Path agent-client.yml -Encoding UTF8;\n"+
                "\n"+
                "# install service and start\n"+
                "echo \\\"3. install...\\\"\n" +
                "cd ..;\n"+
                "cmd.exe /c \\\"install-y20-agent-client-service.bat\\\";\n" +
                "')\n", downloadUrl, agentId, accessToken, serverHost, serverRpcPort, serverRestUseSsl, dataDir);
    }
}
