package com.airxiechao.y20.allinone;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.activity.ActivityBoot;
import com.airxiechao.y20.agent.AgentBoot;
import com.airxiechao.y20.agent.server.AgentServerBoot;
import com.airxiechao.y20.artifact.ArtifactBoot;
import com.airxiechao.y20.auth.AuthBoot;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.cron.CronBoot;
import com.airxiechao.y20.email.EmailBoot;
import com.airxiechao.y20.ip.IpBoot;
import com.airxiechao.y20.log.LogBoot;
import com.airxiechao.y20.manmachinetest.ManMachineTestBoot;
import com.airxiechao.y20.migrate.MongoDdMigrate;
import com.airxiechao.y20.migrate.SqlMigrate;
import com.airxiechao.y20.monitor.MonitorBoot;
import com.airxiechao.y20.pay.PayBoot;
import com.airxiechao.y20.pipeline.PipelineBoot;
import com.airxiechao.y20.pipeline.PipelineRunInstanceBoot;
import com.airxiechao.y20.project.ProjectBoot;
import com.airxiechao.y20.quota.QuotaBoot;
import com.airxiechao.y20.scriptlib.ScriptLibBoot;
import com.airxiechao.y20.sms.SmsBoot;
import com.airxiechao.y20.template.TemplateBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


public class AllInOneBoot {
    private static final Logger logger = LoggerFactory.getLogger(AllInOneBoot.class);
    private static final CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);

    public static void main(String[] args) throws Exception {
        if(null != commonConfig.getServer().getEnableMigrate() && commonConfig.getServer().getEnableMigrate()){
            // migrate databases
            Map<String, Class> migrates = new LinkedHashMap<>();
            migrates.put("sql", SqlMigrate.class);
            migrates.put("mongodb", MongoDdMigrate.class);
            for (Map.Entry<String, Class> entry : migrates.entrySet()) {
                String name = entry.getKey();
                Class cls = entry.getValue();

                logger.info("● migrate database [{}]", name);
                Method mainMethod = cls.getDeclaredMethod("main", String[].class);
                mainMethod.invoke(null, (Object)args);
                Thread.sleep(1000);
            }
        }

        // start modules
        Map<String, Class> modules = new LinkedHashMap<>();
        modules.put("auth", AuthBoot.class);
        modules.put("project", ProjectBoot.class);
        modules.put("pipeline", PipelineBoot.class);
        modules.put("pipeline-run-instance", PipelineRunInstanceBoot.class);
        modules.put("artifact", ArtifactBoot.class);
        modules.put("agent", AgentBoot.class);
        modules.put("log", LogBoot.class);
        modules.put("man-machine-test", ManMachineTestBoot.class);
        modules.put("cron", CronBoot.class);
        modules.put("sms", SmsBoot.class);
        modules.put("quota", QuotaBoot.class);
        modules.put("template", TemplateBoot.class);
        modules.put("activity", ActivityBoot.class);
        modules.put("monitor", MonitorBoot.class);
        modules.put("script-lib", ScriptLibBoot.class);
        modules.put("pay", PayBoot.class);
        modules.put("email", EmailBoot.class);
        modules.put("ip", IpBoot.class);
        modules.put("agent-server", AgentServerBoot.class);

        for (Map.Entry<String, Class> entry : modules.entrySet()) {
            String name = entry.getKey();


            Class cls = entry.getValue();

            logger.info("● start module [{}]", name);
            Method mainMethod = cls.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object)args);
            Thread.sleep(1000);
        }
    }
}
