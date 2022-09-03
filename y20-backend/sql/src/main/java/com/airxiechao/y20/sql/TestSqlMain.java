package com.airxiechao.y20.sql;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.pipeline.db.record.PipelineRecord;
import com.airxiechao.y20.pipeline.db.record.PipelineStepTypeRecord;
import com.airxiechao.y20.pipeline.pojo.*;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestSqlMain {

    public static void main(String[] args){

    }

    public static void test1(){
        String pipelineDbConfigFile = IPipelineDb.class.getAnnotation(IDb.class).value();
        DbManager pipelineDbManager = new DbManager(new JavaResourceFs(), pipelineDbConfigFile);
        pipelineDbManager.updateBySql("delete from " + PipelineRecord.class.getAnnotation(Table.class).value());

        /// type: script
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineId(13L);
        pipeline.setProjectId(9L);
        pipeline.setUserId(4L);
        pipeline.setName("pipeline");
        pipeline.setSteps(Arrays.asList(
                new PipelineStep(){{
                    setName("step-1");
                    setDisabled(false);
                    setType("env");
                    setParam("a", "1");
                    setParam("b", "2");
                }},
                new PipelineStep(){{
                    setName("step-2");
                    setDisabled(true);
                    setType("script");
                    setParam("a", "1");
                    setParam("b", "2");
                }}
        ));

        pipelineDbManager.insert(pipeline.toRecord());
    }


}
