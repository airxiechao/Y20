package com.airxiechao.y20.agent.client;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.y20.agent.rpc.client.AgentRpcClient;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.monitor.scheduler.MonitorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class AgentClientBoot {

    private static final Logger logger = LoggerFactory.getLogger(AgentClientBoot.class);
    private static final AgentClientConfig config = ConfigFactory.get(AgentClientConfig.class);

    public static void main(String[] args) {

        logger.info("agent client working dir [{}]", System.getProperty("user.dir"));

        logger.info("clear tmp");

        try{
            FileUtil.rmDir("tmp");
        }catch (Exception e){
            logger.error("clear tmp error", e);
        }

        // set pty4j.tmpdir
        String pty4jTmpDir = Paths.get(config.getDataDir()).toAbsolutePath().toString();
        System.setProperty("pty4j.tmpdir", pty4jTmpDir);

        // agent rpc client
        AgentRpcClient client = new AgentRpcClient(
                config.getServerHost(),
                config.getAgentId(),
                config.getAccessToken());
        client.start();

        // monitor scheduler
        MonitorScheduler.getInstance().schedule();

        // shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MonitorScheduler.getInstance().shutdown();
            client.stop();
        }));
    }
}
