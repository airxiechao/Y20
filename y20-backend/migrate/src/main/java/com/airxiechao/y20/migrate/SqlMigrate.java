package com.airxiechao.y20.migrate;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.db.api.IAgentDb;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;
import com.airxiechao.y20.common.core.db.DbManagerUtil;
import com.airxiechao.y20.common.git.GitUtil;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.MigrateConfig;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.migrate.util.SqlUtil;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.pipeline.db.record.PipelineStepTypeRecord;
import com.airxiechao.y20.pipeline.pojo.PipelineStepType;
import com.airxiechao.y20.pipeline.pojo.PipelineStepTypeParam;
import com.airxiechao.y20.pipeline.pojo.PipelineStepTypeSelectOption;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlMigrate {
    private static final MigrateConfig migrateConfig = ConfigFactory.get(CommonConfig.class).getMigrate();
    private static final Logger logger = LoggerFactory.getLogger(SqlMigrate.class);

    public static void main(String[] args) throws Exception {
        migrateDb();
        migrateData();
    }

    public static void migrateDb() throws Exception {
        DbManager dbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), "mybatis-no-database.xml");

        // create database
        logger.info("create databases");
        SqlUtil.createDb(dbManager, false, true);

        // create dummy database
        logger.info("create dummy databases");
        SqlUtil.createDb(dbManager, true, true);

        // diff and update
        CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);
        String username = commonConfig.getMysql().getUsername();
        String password = commonConfig.getMysql().getPassword();

        List<String> dbNames = SqlUtil.getAllDbNames();
        for (String dbName : dbNames) {
            logger.info("migrate database [{}]", dbName);
            SqlUtil.diffAndUpdateDb(dbManager, username, password, dbName);
        }
    }

    public static void migrateData() throws Exception {
        migrateStepType();
//        migratePayPrice();

        if(null != migrateConfig && migrateConfig.getMigrateAgentVersion()){
            migrateAgentVersion();
        }
    }

    public static void migrateStepType() {
        String pipelineDbConfigFile = IPipelineDb.class.getAnnotation(IDb.class).value();
        DbManager pipelineDbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), pipelineDbConfigFile);
//        pipelineDbManager.updateBySql("delete from " + PipelineStepTypeRecord.class.getAnnotation(Table.class).value());
        long count = pipelineDbManager.longBySql("select count(*) from " + PipelineStepTypeRecord.class.getAnnotation(Table.class).value());
        if (count == 0) {
            logger.info("initialize step type table");

            // step type
            Set<Class<?>> stepRuns = ClsUtil.getTypesAnnotatedWith(Meta.getProjectPackageName(), StepRun.class);
            for (Class<?> stepRun : stepRuns) {
                StepRun annotation = stepRun.getAnnotation(StepRun.class);
                boolean visible = annotation.visible();
                if (!visible) {
                    continue;
                }

                PipelineStepType stepType = getStepTypeFromClass(stepRun);
                pipelineDbManager.insert(stepType.toRecord());
            }
        }
    }

//    public static void migratePayPrice() {
//        String payDbConfigFile = IPayDb.class.getAnnotation(IDb.class).value();
//        DbManager payDbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), payDbConfigFile);
//        long count = payDbManager.longBySql("select count(*) from " + PayPriceRecord.class.getAnnotation(Table.class).value());
//        if (count == 0) {
//            logger.info("initialize pay price table");
//            PayPriceRecord agentQuotaPriceRecord = new PayPriceRecord(EnumPayPriceBillingPlan.BASIC, 15, 600, 4900);
//            PayPriceRecord pipelineRunQuotaPriceRecord = new PayPriceRecord(EnumPayPriceBillingPlan.PRO, 50, 2000, 9900);
//            payDbManager.insert(agentQuotaPriceRecord);
//            payDbManager.insert(pipelineRunQuotaPriceRecord);
//        }
//    }

    public static void migrateAgentVersion() {
        String agentDbConfigFile = IAgentDb.class.getAnnotation(IDb.class).value();
        DbManager agentDbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), agentDbConfigFile);

        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentVersionRecord.class))
                .order(DbUtil.column(AgentVersionRecord.class, "releaseTime"), OrderType.DESC)
                .page(1, 1);
        SqlParams sqlParams = sqlParamsBuilder.build();

        AgentVersionRecord record = agentDbManager.selectFirstBySql(sqlParams.getSql(), AgentVersionRecord.class);
        String gitVersion = GitUtil.getGitVersion();
        if (null != record && !gitVersion.equals(record.getVersion())) {
            agentDbManager.updateBySql("delete from " + AgentVersionRecord.class.getAnnotation(Table.class).value());
        }

        long count = agentDbManager.longBySql("select count(*) from " + AgentVersionRecord.class.getAnnotation(Table.class).value());
        if (count == 0) {
            logger.info("initialize agent version table");
            AgentVersionRecord agentVersionRecord = new AgentVersionRecord(
                    gitVersion,
                    "/artifacts/y20-agent-client-win.zip",
                    "/artifacts/y20-agent-client-linux.zip",
                    "/artifacts/y20-agent-client-upgrader-win.zip",
                    "/artifacts/y20-agent-client-upgrader-linux.zip",
                    GitUtil.getGitBuildTime()
            );
            agentDbManager.insert(agentVersionRecord);
        }
    }

    private static PipelineStepType getStepTypeFromClass(Class<?> cls) {
        try {
            StepRun stepRun = cls.getAnnotation(StepRun.class);
            String type = stepRun.type();
            String name = stepRun.name();
            String icon = stepRun.icon();
            String description = stepRun.description();
            String category = stepRun.category();
            int order = stepRun.order();
            Class paramClass = stepRun.paramClass();
            String[] outputs = stepRun.outputs();
            String[] requires = stepRun.requires();

            List<PipelineStepTypeParam> paramsList = new ArrayList<>();
            Set<Field> fields = ClsUtil.getFields(paramClass, StepTypeParam.class);
            for (Field field : fields) {
                field.setAccessible(true);

                StepTypeParam paramMeta = field.getAnnotation(StepTypeParam.class);
                Required requiredMeta = field.getAnnotation(Required.class);
                String metaName = paramMeta.name();
                String metaDisplayName = paramMeta.displayName();
                int metaDisplayOrder = paramMeta.displayOrder();
                String metaHint = paramMeta.hint();
                String metaType = paramMeta.type();
                String defaultValue = paramMeta.defaultValue();
                StepTypeSelectOption[] options = paramMeta.options();
                boolean metaRequired = null != requiredMeta;
                String metaConditionalOnRequiredTrue = metaRequired ? requiredMeta.conditionalOnRequiredTrue() : null;

                String paramName = StringUtil.isBlank(metaName) ? field.getName() : metaName;

                PipelineStepTypeParam param = new PipelineStepTypeParam();
                param.setName(paramName);
                param.setDisplayName(metaDisplayName);
                param.setDisplayOrder(metaDisplayOrder);
                param.setHint(metaHint);
                param.setType(metaType);
                param.setRequired(metaRequired);
                param.setConditionalOnRequiredTrue(metaConditionalOnRequiredTrue);
                param.setDefaultValue(defaultValue);
                param.setOptions(Arrays.stream(options)
                        .map(o -> new PipelineStepTypeSelectOption(o.label(), o.value()))
                        .collect(Collectors.toList()));

                paramsList.add(param);
            }

            PipelineStepType stepType = new PipelineStepType();
            stepType.setName(name);
            stepType.setType(type);
            stepType.setIcon(icon);
            stepType.setDescription(description);
            stepType.setCategory(category);
            stepType.setOrder(order);
            stepType.setParams(paramsList);
            stepType.setOutputs(Arrays.asList(outputs));
            stepType.setRequires(Arrays.asList(requires));

            return stepType;
        } catch (Exception e) {
            return null;
        }
    }
}
