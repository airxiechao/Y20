package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.TimeUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.db.record.PipelineWebhookTriggerRecord;
import com.airxiechao.y20.pipeline.pojo.PipelineWebhookTrigger;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineWebhookTriggerEventType;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.api.IWebhookPipelineRest;
import com.airxiechao.y20.pipeline.rest.param.CreatePipelineRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.param.StartPipelineRunInstanceParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebhookPipelineRestHandler implements IWebhookPipelineRest {

    private static final Logger logger = LoggerFactory.getLogger(WebhookPipelineRestHandler.class);
    private static final Pattern tagPattern = Pattern.compile("#y20-([^\\s#]+)");
    private static final String holderNameRegex = "\\{\\{([^\\{\\}]*)\\}\\}";
    private static final Pattern holderNamePattern = Pattern.compile(holderNameRegex);

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    @Override
    public Response triggerPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        JSONObject param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, JSONObject.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        Long userId = param.getLong("userId");

        // find all webhook triggers
        List<PipelineWebhookTriggerRecord> triggerRecords = pipelineBiz.listPipelineWebhookTrigger(userId, null, null, null, null, null, null);

        triggerRecords.stream().forEach(record -> {
            PipelineWebhookTrigger trigger = record.toPojo();

            String sourceType = trigger.getSourceType();
            String eventType = trigger.getEventType();
            String triggerTag = trigger.getTag();

            switch (eventType){
                case EnumPipelineWebhookTriggerEventType.PUSH:
                    String message = getGitPushHeadCommitMessage(param);
                    if(StringUtil.isBlank(message)){
                        return;
                    }

                    List<String> tags = extractTags(message);
                    tags.forEach(tag -> {
                        if(tagMatches(triggerTag, tag)){
                            logger.info("webhook trigger pipeline -> user: [{}], tag: [#y20-{}], payload: [{}]", userId, tag, JSON.toJSONString(param));

                            // extract tag holders
                            Map<String, String> holders = extractTagHolders(triggerTag, tag);

                            // replace inParams value with holders
                            Map<String, String> runInParams = replaceInParamsWithHolders(trigger.getInParams(), holders);

                            try {
                                String runName = String.format("触发 #y20-%s", tag);
                                PipelineRunRecord pipelineRunRecord = triggerPipelineRun(trigger.getUserId(), trigger.getProjectId(), trigger.getPipelineId(),
                                        runName, runInParams, false);
                                pipelineBiz.updatePipelineWebhookTriggerLastTrigger(
                                        trigger.getUserId(), trigger.getProjectId(), trigger.getPipelineId(), trigger.getPipelineWebhookTriggerId(),
                                        pipelineRunRecord.getCreateTime(), pipelineRunRecord.getId()
                                );
                            } catch (Exception e) {
                                logger.error("webhook trigger pipeline run error", e);
                            }
                        }
                    });
                    break;
            }
        });

        return new Response();
    }

    private String getGitPushHeadCommitMessage(JSONObject payload){
        JSONObject headCommit = payload.getJSONObject("head_commit");
        if(null == headCommit){
            return null;
        }

        String message = headCommit.getString("message");
        return message;
    }

    private List<String> extractTags(String message){
        List<String> tags = new ArrayList<>();
        Matcher matcher = tagPattern.matcher(message);
        while (matcher.find()){
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private boolean tagMatches(String triggerTag, String tag){
        String triggerTagRegex = triggerTag.replaceAll(holderNameRegex, "(.*)");
        boolean tagMatched = tag.matches(triggerTagRegex);
        return tagMatched;
    }

    private Map<String, String> extractTagHolders(String triggerTag, String tag){
        List<String> holderNames = new ArrayList<>();
        List<String> holderValues = new ArrayList<>();

        Matcher holderNameMatcher = holderNamePattern.matcher(triggerTag);
        while (holderNameMatcher.find()){
            String holderName = holderNameMatcher.group(1);
            holderNames.add(holderName);
        }

        String triggerTagRegex = triggerTag.replaceAll(holderNameRegex, "(.*)");
        Pattern triggerTagPattern = Pattern.compile(triggerTagRegex);
        Matcher holderValueMatcher = triggerTagPattern.matcher(tag);
        if(holderValueMatcher.find()){
            for(int i = 0; i < holderValueMatcher.groupCount(); ++i){
                String holderValue = holderValueMatcher.group(i+1);
                holderValues.add(holderValue);
            }
        }

        Map<String, String> holders = new HashMap<>();
        for(int i = 0; i < holderNames.size(); ++i){
            String holderName = holderNames.get(i);
            String holderValue = holderValues.get(i);
            holders.put(holderName, holderValue);
        }

        return holders;
    }

    private Map<String, String> replaceInParamsWithHolders(Map<String, String> inParams, Map<String, String> holders){
        Map<String, String> newInParams = new HashMap<>();

        for(Map.Entry<String, String> entry : inParams.entrySet()){
            String name = entry.getKey();
            String value = entry.getValue();

            for(Map.Entry<String, String> holder : holders.entrySet()){
                value = value.replaceAll("\\{\\{\\s*" + holder.getKey() + "\\s*\\}\\}", holder.getValue());
            }

            newInParams.put(name, value);
        }

        return newInParams;
    }

    private PipelineRunRecord triggerPipelineRun(
            Long userId, Long projectId, Long pipelineId,
            String name, Map<String, String> inParams, Boolean flagDebug
    ) throws Exception{
        // create run record
        PipelineRunRecord pipelineRunRecord = null;
        pipelineRunRecord = pipelineBiz.createPipelineRun(
                userId, projectId, pipelineId,
                name, inParams, flagDebug);
        if(null == pipelineRunRecord){
            throw new Exception("create pipeline run error");
        }

        Long pipelineRunId = pipelineRunRecord.getId();

        // create run instance
        Response createRunInstanceResp = ServiceRestClient.get(IServicePipelineRunInstanceRest.class).createPipelineRunInstance(
                new CreatePipelineRunInstanceParam(pipelineRunId));
        if(!createRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunId, EnumPipelineRunStatus.STATUS_FAILED, createRunInstanceResp.getMessage(), null);
            throw new Exception("create pipeline run instance error");
        }

        // start run instance
        Response startRunInstanceResp = ServicePipelineRunInstanceRestClient.get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid()).startPipelineRunInstance(
                new StartPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid(), pipelineRunRecord.getFlagDebug()));
        if(!startRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunId, EnumPipelineRunStatus.STATUS_FAILED, startRunInstanceResp.getMessage(), null);
            throw new Exception("start pipeline run instance error");
        }

        return pipelineRunRecord;
    }
}
