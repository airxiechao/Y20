package com.airxiechao.y20.sql;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.db.api.IUserDb;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.pay.db.api.IPayDb;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.pojo.constant.EnumPayPriceBillingPlan;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.pipeline.db.record.PipelineStepTypeRecord;
import com.airxiechao.y20.pipeline.pojo.PipelineStepType;
import com.airxiechao.y20.pipeline.pojo.PipelineStepTypeParam;
import com.airxiechao.y20.pipeline.pojo.PipelineStepTypeSelectOption;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InitializeDataSqlMain {

    public static void main(String[] args){
        initStepType();

        //initPayPrice();
    }

    public static void initPayPrice(){
        String payDbConfigFile = IPayDb.class.getAnnotation(IDb.class).value();
        DbManager payDbManager = new DbManager(new JavaResourceFs(), payDbConfigFile);
        payDbManager.updateBySql("delete from " + PayPriceRecord.class.getAnnotation(Table.class).value());

        PayPriceRecord agentQuotaPriceRecord = new PayPriceRecord(EnumPayPriceBillingPlan.BASIC, 15, 600, 4900);
        PayPriceRecord pipelineRunQuotaPriceRecord = new PayPriceRecord(EnumPayPriceBillingPlan.PRO, 50, 2000, 9900);
        payDbManager.insert(agentQuotaPriceRecord);
        payDbManager.insert(pipelineRunQuotaPriceRecord);
    }

    public static void initStepType(){
        String pipelineDbConfigFile = IPipelineDb.class.getAnnotation(IDb.class).value();
        DbManager pipelineDbManager = new DbManager(new JavaResourceFs(), pipelineDbConfigFile);
        pipelineDbManager.updateBySql("delete from " + PipelineStepTypeRecord.class.getAnnotation(Table.class).value());

        // step type
        Set<Class<?>> stepRuns = ClsUtil.getTypesAnnotatedWith(Meta.getProjectPackageName(), StepRun.class);
        for (Class<?> stepRun : stepRuns) {
            StepRun annotation = stepRun.getAnnotation(StepRun.class);
            boolean visible = annotation.visible();
            if(!visible){
                continue;
            }
            PipelineStepType stepType = getStepTypeFromClass(stepRun);
            pipelineDbManager.insert(stepType.toRecord());
        }
    }

    private static PipelineStepType getStepTypeFromClass(Class<?> cls){
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
            for(Field field : fields){
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
                        .map(o -> new PipelineStepTypeSelectOption(o.label(),o.value()))
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
