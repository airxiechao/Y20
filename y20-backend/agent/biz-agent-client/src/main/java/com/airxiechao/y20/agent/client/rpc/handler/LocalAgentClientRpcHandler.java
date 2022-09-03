package com.airxiechao.y20.agent.client.rpc.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.communication.rpc.util.RpcUtil;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.axcboot.util.HttpUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.rpc.api.client.ILocalAgentClientRpc;
import com.airxiechao.y20.agent.rpc.param.*;
import com.airxiechao.y20.common.core.pool.AgentRpcClientThreadPool;
import com.airxiechao.y20.util.LinuxUtil;
import com.airxiechao.y20.util.OsUtil;
import com.airxiechao.y20.util.WindowsUtil;
import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class LocalAgentClientRpcHandler implements ILocalAgentClientRpc {

    private static final Logger logger = LoggerFactory.getLogger(LocalAgentClientRpcHandler.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private ThreadPoolExecutor executor = AgentRpcClientThreadPool.get().getExecutor();

    @Override
    public Response upgrade(Object exc) throws Exception {
        RpcExchange exchange = (RpcExchange)exc;

        UpgradeAgentClientRpcParam param = null;
        try {
            param = RpcUtil.getObjectParam(exchange, UpgradeAgentClientRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // mkdir
        String tmpDir = "tmp/" + UUID.randomUUID();
        FileUtil.mkDirs(tmpDir);

        logger.info("agent client upgrade [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        String downloadUrl = param.getDownloadUrl();
        String downloadUpgraderUrl = param.getDownloadUpgraderUrl();

        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {

            // download and unzip new agent client release
            logger.info("download new release [{}]", downloadUrl);
            File releaseFile = Paths.get(tmpDir, "y20-agent-client.zip").toFile();
            try(OutputStream outputStream = new FileOutputStream(releaseFile)){
                HttpUtil.download(downloadUrl, null, null, null, 15, false, outputStream, (total, speed) -> {
                    logger.info("download total: {} kB, speed: {} kB/s", total / 1024, speed / 1024);
                }, null);
            }catch (Exception e){
                logger.error("download new release error", e);
                return new Response().error("download new release error: " + e.getMessage());
            }

            try{
                logger.info("unzip new release");
                new ZipFile(releaseFile).extractAll(tmpDir);
            }catch (Exception e){
                logger.error("unzip new release error", e);
                return new Response().error("unzip new release error: " + e.getMessage());
            }

            // download and unzip upgrader
            logger.info("download upgrader [{}]", downloadUpgraderUrl);
            File upgraderFile = Paths.get(tmpDir, "y20-agent-client-upgrader.zip").toFile();
            try(OutputStream outputStream = new FileOutputStream(upgraderFile)){
                HttpUtil.download(downloadUpgraderUrl, null, null, null, 15, false, outputStream, (total, speed) -> {
                    logger.error("download total: {} kB, speed: {} kB/s", total / 1024, speed / 1024);
                }, null);
            }catch (Exception e){
                logger.error("download upgrader error", e);
                return new Response().error("download upgrader error: " + e.getMessage());
            }

            try{
                logger.info("unzip upgrader");
                new ZipFile(upgraderFile).extractAll(tmpDir);
            }catch (Exception e){
                logger.error("unzip upgrader error", e);
                return new Response().error("unzip upgrader error: " + e.getMessage());
            }

            // run upgrader
            logger.info("run upgrader");

            try {
                String os = OsUtil.getOs();
                switch (os) {
                    case OsUtil.OS_WINDOWS:
                        String batScript = Files.readString(Paths.get(tmpDir, "y20-agent-client-upgrader", "upgrade.bat"));
                        WindowsUtil.executeScriptAsync(batScript, null, tmpDir, null);

                        break;
                    case OsUtil.OS_LINUX:
                        String shScript = Files.readString(Paths.get(tmpDir, "y20-agent-client-upgrader", "upgrade.sh"));
                        LinuxUtil.executeScriptAsync(shScript, null, tmpDir, null);
                        break;
                    default:
                        throw new Exception("os not supported");
                }
            } catch (Exception e){
                logger.error("run upgrader error", e);
                return new Response().error("run upgrader error: " + e.getMessage());
            }

            return new Response();

        }, executor);

        return new Response();
    }

    @Override
    public Response uninstall(Object exc) throws Exception {
        RpcExchange exchange = (RpcExchange)exc;

        UninstallAgentClientRpcParam param = null;
        try {
            param = RpcUtil.getObjectParam(exchange, UninstallAgentClientRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        logger.info("agent client uninstall [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {

            // run upgrader
            logger.info("uninstall");

            try {
                String os = OsUtil.getOs();
                switch (os) {
                    case OsUtil.OS_WINDOWS:
                        String batScript = Files.readString(Paths.get("uninstall-y20-agent-client-service.bat"));
                        WindowsUtil.executeScriptAsync(batScript, null, ".", null);

                        break;
                    case OsUtil.OS_LINUX:
                        String shScript = Files.readString(Paths.get("uninstall-y20-agent-client-service.sh"));
                        LinuxUtil.executeScriptAsync(shScript, null, ".", null);
                        break;
                    default:
                        throw new Exception("os not supported");
                }
            } catch (Exception e){
                logger.error("uninstall error", e);
                return new Response().error("uninstall error: " + e.getMessage());
            }

            return new Response();

        }, executor);

        return new Response();
    }

    @Override
    public Response restart(Object exc) throws Exception {
        RpcExchange exchange = (RpcExchange)exc;

        RestartAgentClientRpcParam param = null;
        try {
            param = RpcUtil.getObjectParam(exchange, RestartAgentClientRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        logger.info("agent client restart [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {

            // restart
            logger.info("restart");

            try {
                String os = OsUtil.getOs();
                switch (os) {
                    case OsUtil.OS_WINDOWS:
                        String batScript = Files.readString(Paths.get("restart-y20-agent-client-service.bat"));
                        WindowsUtil.executeScriptAsync(batScript, null, ".", null);

                        break;
                    case OsUtil.OS_LINUX:
                        String shScript = Files.readString(Paths.get("restart-y20-agent-client-service.sh"));
                        LinuxUtil.executeScriptAsync(shScript, null, ".", null);
                        break;
                    default:
                        throw new Exception("os not supported");
                }
            } catch (Exception e){
                logger.error("restart error", e);
                return new Response().error("restart error: " + e.getMessage());
            }

            return new Response();

        }, executor);

        return new Response();
    }

    @Override
    public Response<String> readConfig(Object exc) throws Exception {
        RpcExchange exchange = (RpcExchange)exc;

        ReadAgentClientConfigRpcParam param = null;
        try {
            param = RpcUtil.getObjectParam(exchange, ReadAgentClientConfigRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // read config
        Path configPath = Paths.get("conf", "agent-client.yml");
        if(Files.exists(configPath)){
            String config = Files.readString(configPath);
            return new Response<String>().data(config);
        }

        IFs classFs = new JavaResourceFs();
        if(classFs.exist("agent-client.yml")){
            try(InputStream inputStream = classFs.getInputStream("agent-client.yml")){
                String config = StreamUtil.readString(inputStream, StandardCharsets.UTF_8);
                return new Response<String>().data(config);
            }
        }

        throw new Exception("no agent client config");
    }

    @Override
    public Response saveConfig(Object exc) throws Exception {
        RpcExchange exchange = (RpcExchange)exc;

        SaveAgentClientConfigRpcParam param = null;
        try {
            param = RpcUtil.getObjectParam(exchange, SaveAgentClientConfigRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // write config
        String config = param.getConfig();
        Path configPath = Paths.get("conf", "agent-client.yml");
        if(Files.exists(configPath)){
            Files.writeString(configPath, config);
            return new Response();
        }

        throw new Exception("no agent client config");
    }

    @Override
    public Response clean(Object exc) throws Exception {
        Path cachePath = Paths.get(agentClientConfig.getDataDir(), "cache");
        Path workspacePath = Paths.get(agentClientConfig.getDataDir(), "workspace");

        logger.info("delete cache");
        boolean cacheDeleted = FileUtil.rmDir(cachePath.toFile());
        if(!cacheDeleted){
            logger.error("delete cache error");
            return new Response().error("delete cache error");
        }

        logger.info("delete workspace");
        boolean workspaceDeleted = FileUtil.rmDir(workspacePath.toFile());
        if(!workspaceDeleted){
            logger.error("delete workspace error");
            return new Response().error("delete workspace error");
        }

        return new Response();
    }
}
