package com.airxiechao.y20.pipeline.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.axcboot.storage.cache.expire.ExpiringCache;
import com.airxiechao.axcboot.storage.cache.expire.ExpiringCacheManager;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.VariableCommonConfig;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.pipeline.db.record.*;
import com.airxiechao.y20.pipeline.pojo.*;
import com.airxiechao.y20.pipeline.pojo.config.PipelineConfig;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineStepRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineVariableKind;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.pojo.exception.NoEnoughQuotaException;
import com.airxiechao.y20.pipeline.pojo.exception.OnlyOneRunException;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineVariableVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineWithLastRunVo;
import com.airxiechao.y20.pipeline.pubsub.event.cron.EventPipelineCronUpdate;
import com.airxiechao.y20.pipeline.run.step.param.RemotePrepareEnvStepRunParam;
import com.airxiechao.y20.quota.rest.api.IServiceQuotaRest;
import com.airxiechao.y20.quota.rest.param.ServiceValidateQuotaParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PipelineBizProcess implements IPipelineBiz {

    private static final Logger logger = LoggerFactory.getLogger(PipelineBizProcess.class);
    private static final PipelineConfig pipelineConfig = ConfigFactory.get(PipelineConfig.class);

    private IPipelineDb pipelineDb = Db.get(IPipelineDb.class);
    private VariableCommonConfig variableCommonConfig = ConfigFactory.get(CommonConfig.class).getVariable();

    private static final ExpiringCache<PipelineStepTypeRecord> stepTypeCache = ExpiringCacheManager.getInstance().createCache(
            "step-type-cache", 6, ExpiringCache.UNIT.HOUR);

    @Override
    public List<PipelineRecord> list(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return pipelineDb.list(userId, projectId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public List<PipelineRecord> listScheduledPipeline(Long fromPipelineId, Long toPipelineId) {
        return pipelineDb.listScheduledPipeline(fromPipelineId, toPipelineId);
    }

    @Override
    public List<PipelineWithLastRunVo> listWithLastRun(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return pipelineDb.listWithLastRun(userId, projectId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long count(Long userId, Long projectId, String name) {
        return pipelineDb.count(userId, projectId, name);
    }

    @Override
    public boolean hasMore(Long fromPipelineId) {
        return pipelineDb.hasMore(fromPipelineId);
    }

    @Override
    public PipelineRecord create(Pipeline pipeline) {
        PipelineRecord record = pipeline.toRecord();
        boolean created = pipelineDb.insert(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public PipelineRecord create(
            Long userId, Long projectId, String name, String description, Boolean flagOneRun,
            List<PipelineStep> steps, Map<String, PipelineVariable> variables
    ) {
        if(null == steps){
            steps = new ArrayList<>();
        }

        if(null == variables){
            variables = new HashMap<>();
        }

        Pipeline pipeline = new Pipeline();
        pipeline.setUserId(userId);
        pipeline.setProjectId(projectId);
        pipeline.setName(name);
        pipeline.setDescription(description);
        pipeline.setSteps(steps);
        pipeline.setVariables(variables);
        pipeline.setFlagOneRun(flagOneRun);
        pipeline.setFlagInjectProjectVariable(true);
        pipeline.setFlagCronEnabled(false);
        pipeline.setCronInParams(new HashMap<>());
        pipeline.setBookmarked(false);
        pipeline.setDeleted(false);
        pipeline.setCreateTime(new Date());

        PipelineRecord record = pipeline.toRecord();
        boolean created = pipelineDb.insert(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public PipelineRecord copy(Long userId, Long projectId, Long pipelineId, Long toProjectId, String toPipelineName) {
        PipelineRecord record = pipelineDb.get(userId, projectId, pipelineId);

        record.setId(null);
        record.setProjectId(toProjectId);
        record.setName(toPipelineName);
        record.setDescription(record.getDescription());
        record.setFlagCronEnabled(false);
        record.setDeleted(false);
        record.setCreateTime(new Date());

        boolean created = pipelineDb.insert(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public PipelineRecord get(Long userId, Long projectId, Long pipelineId) {
        return pipelineDb.get(userId, projectId, pipelineId);
    }

    @Override
    public PipelineRecord get(Long pipelineId) {
        return pipelineDb.get(pipelineId);
    }

    @Override
    public boolean update(Long userId, Long projectId, Long pipelineId, String name, String description, Boolean flagOneRun, Boolean flagInjectProjectVariable,
                          Boolean flagCronEnabled, Date cronBeginTime, Integer cronIntervalSecs, Map<String, String> cronInParams) {
        PipelineRecord pipelineRecord = pipelineDb.get(userId, projectId, pipelineId);
        if(null == pipelineRecord){
            return false;
        }

        Pipeline pipeline = pipelineRecord.toPojo();

        pipeline.setName(name);
        pipeline.setDescription(description);
        pipeline.setFlagOneRun(flagOneRun);
        pipeline.setFlagInjectProjectVariable(flagInjectProjectVariable);
        pipeline.setFlagCronEnabled(flagCronEnabled);
        pipeline.setCronBeginTime(cronBeginTime);
        pipeline.setCronIntervalSecs(cronIntervalSecs);
        pipeline.setCronInParams(cronInParams);

        boolean updated = pipelineDb.update(pipeline.toRecord());
        if(!updated){
            return false;
        }

        // cron updated event
        EventBus.getInstance().publish(new EventPipelineCronUpdate(pipelineId, flagCronEnabled, cronBeginTime, cronIntervalSecs, cronInParams));

        return true;
    }

    @Override
    public boolean updateBookmark(Long userId, Long projectId, Long pipelineId, boolean bookmarked) {
        PipelineRecord pipelineRecord = pipelineDb.get(userId, projectId, pipelineId);
        if(null == pipelineRecord){
            return false;
        }

        Pipeline pipeline = pipelineRecord.toPojo();

        pipeline.setBookmarked(bookmarked);
        if(bookmarked){
            pipeline.setBookmarkTime(new Date());
        }else {
            pipeline.setBookmarkTime(null);
        }

        boolean updated = pipelineDb.update(pipeline.toRecord());
        if(!updated){
            return false;
        }

        return true;
    }

    @Override
    public boolean updatePublishedTemplate(Long userId, Long projectId, Long pipelineId, Long publishedTemplateId) {
        PipelineRecord pipelineRecord = pipelineDb.get(userId, projectId, pipelineId);
        if(null == pipelineRecord){
            return false;
        }

        pipelineRecord.setPublishedTemplateId(publishedTemplateId);

        boolean updated = pipelineDb.update(pipelineRecord);
        if(!updated){
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(Long userId, Long projectId, Long pipelineId) {
        PipelineRecord pipelineRecord = pipelineDb.get(userId, projectId, pipelineId);
        if(null == pipelineRecord){
            return false;
        }

        pipelineRecord.setDeleted(true);
        pipelineRecord.setDeleteTime(new Date());

        boolean updated = pipelineDb.update(pipelineRecord);
        if(!updated){
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(Long projectId) {
        List<PipelineRecord> pipelineRecordList = pipelineDb.list(
                projectId, null, null, null, null, null);

        pipelineRecordList.forEach(pipelineRecord -> {
            pipelineRecord.setDeleted(true);
            pipelineRecord.setDeleteTime(new Date());
            boolean updated = pipelineDb.update(pipelineRecord);
            if(!updated){
                logger.error("delete project [{}]'s pipeline [{}] error", projectId, pipelineRecord.getId());
            }
        });

        return true;
    }

    @Override
    public boolean addStep(Long userId, Long projectId, Long pipelineId, Integer position, PipelineStep step) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        if(null == steps){
            steps = new ArrayList<>();
            pipeline.setSteps(steps);
        }
        if(null == position){
            steps.add(step);
        }else{
            steps.add(position, step);
        }

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean copyStep(Long userId, Long projectId, Long pipelineId, Integer srcPosition, Integer destPosition) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        PipelineStep srcStep = steps.get(srcPosition);
        PipelineStep destStep = ModelUtil.deepCopy(srcStep, PipelineStep.class);
        destStep.setName(destStep.getName() + "-复制");

        steps.add(destPosition, destStep);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean moveStep(Long userId, Long projectId, Long pipelineId, int srcPosition, int destPosition) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        PipelineStep srcStep = steps.get(srcPosition);

        steps.remove(srcPosition);
        steps.add(destPosition, srcStep);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean disableStep(Long userId, Long projectId, Long pipelineId, int position, boolean disabled) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        steps.get(position).setDisabled(disabled);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean updateStepCondition(Long userId, Long projectId, Long pipelineId, int position, String condition) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        steps.get(position).setCondition(condition);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean updateStep(Long userId, Long projectId, Long pipelineId, int position, PipelineStep step) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        steps.set(position, step);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public boolean deleteStep(Long userId, Long projectId, Long pipelineId, int position) {
        Pipeline pipeline = pipelineDb.get(userId, projectId, pipelineId).toPojo();

        List<PipelineStep> steps = pipeline.getSteps();
        steps.remove(position);

        PipelineRecord record = pipeline.toRecord();
        return pipelineDb.update(record);
    }

    @Override
    public List<PipelineStepTypeRecord> listStepType() {
        return pipelineDb.listStepType();
    }

    @Override
    public PipelineStepTypeRecord getStepType(String type) {
        PipelineStepTypeRecord typeRecord = stepTypeCache.get(type);
        if(null == typeRecord){
            typeRecord = pipelineDb.getStepType(type);
            stepTypeCache.put(type, typeRecord);
        }
        return typeRecord;
    }

    @Override
    public PipelineRunRecord createPipelineRun(Long userId, Long projectId, Long pipelineId, String name,
                                               Map<String, String> inParams, Boolean flagDebug) throws Exception {
        PipelineRecord pipelineRecord = get(userId, projectId, pipelineId);
        Pipeline pipeline = pipelineRecord.toPojo();

        return createPipelineRun(pipeline, name, inParams, flagDebug);
    }

    @Override
    public PipelineRunRecord createPipelineRunPty(Long userId, String agentId, Boolean flagDebug) throws Exception {
        PipelineStep ptyPipelineStep = new PipelineStep()
                .setType(EnumStepRunType.REMOTE_PREPARE_ENV_TYPE)
                .setParams(ModelUtil.toMapAndCheckRequiredField(new RemotePrepareEnvStepRunParam(
                        agentId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        false,
                        false
                )));
        ptyPipelineStep.setDisabled(false);

        Pipeline ptyPipeline = new Pipeline()
                .addStep(ptyPipelineStep);
        ptyPipeline.setUserId(userId);
        ptyPipeline.setProjectId(0L);
        ptyPipeline.setPipelineId(0L);
        String name = String.format("pty-%s", agentId);

        return createPipelineRun(ptyPipeline, name, new HashMap<>(), flagDebug);
    }

    @Override
    public PipelineRunRecord createPipelineRun(Pipeline pipeline, String name, Map<String, String> inParams, Boolean flagDebug) throws Exception {

        if(pipelineConfig.getEnableQuota()){
            // check if has enough quota
            Response quotaResp = ServiceRestClient.get(IServiceQuotaRest.class).validateQuota(
                    new ServiceValidateQuotaParam(pipeline.getUserId()));
            if(!quotaResp.isSuccess()){
                String error = String.format("no enough quota: %s", quotaResp.getMessage());
                logger.error(error);
                throw new NoEnoughQuotaException(error);
            }
        }

        // check if permit only one run at a time
        if(null != pipeline.getFlagOneRun() && pipeline.getFlagOneRun()){
            long count = pipelineDb.countPipelineRun(pipeline.getUserId(), pipeline.getProjectId(), pipeline.getPipelineId(), null, null, true, true);
            if(count > 0){
                String error = "run pending: permit only one run at a time";
                logger.error(error);

                // enqueue
                PipelinePendingRecord pipelinePendingRecord = createPipelinePending(pipeline.getUserId(), pipeline.getProjectId(), pipeline.getPipelineId(),
                        name, inParams, flagDebug, "同时只允许启动一个运行实例");
                logger.info("enqueue pipeline [{}] pending -> [{}]", pipeline.getPipelineId(), pipelinePendingRecord.getId());

                throw new OnlyOneRunException(error);
            }
        }

        // create pipeline run
        List<PipelineStep> steps = pipeline.getSteps();
        if(null != steps){
            steps = steps.stream().filter(step -> !step.getDisabled()).collect(Collectors.toList());
            pipeline.setSteps(steps);
        }

        PipelineRun pipelineRun = new PipelineRun();
        pipelineRun.setName(name);
        pipelineRun.setUserId(pipeline.getUserId());
        pipelineRun.setProjectId(pipeline.getProjectId());
        pipelineRun.setPipelineId(pipeline.getPipelineId());
        pipelineRun.setInstanceUuid(UuidUtil.random());
        pipelineRun.setPipeline(pipeline);
        pipelineRun.setStatus(EnumPipelineRunStatus.STATUS_CREATED);
        pipelineRun.setInParams(inParams);
        pipelineRun.setFlagDebug(flagDebug);
        pipelineRun.setDeleted(false);
        pipelineRun.setCreateTime(new Date());

        PipelineRunRecord pipelineRunRecord = pipelineRun.toRecord();;
        pipelineDb.createPipelineRun(pipelineRunRecord);

        // create step runs
        if(null != steps){
            for(int i = 0; i < steps.size(); ++i){
                PipelineStep step = steps.get(i);

                PipelineStepRunRecord stepRunRecord = new PipelineStepRunRecord();
                stepRunRecord.setPipelineRunId(pipelineRunRecord.getId());
                stepRunRecord.setInstanceUuid(UuidUtil.random());
                stepRunRecord.setName(step.getName());
                stepRunRecord.setType(step.getType());
                stepRunRecord.setPosition(i);
                stepRunRecord.setStatus(EnumPipelineStepRunStatus.STATUS_CREATED);

                pipelineDb.createPipelineStepRun(stepRunRecord);
            }
        }

        return pipelineRunRecord;
    }

    @Override
    public PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId) {
        return pipelineDb.getPipelineRun(userId, projectId, pipelineId, pipelineRunId);
    }

    @Override
    public PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, String pipelineRunInstanceUuid) {
        return pipelineDb.getPipelineRun(userId, projectId, pipelineId, pipelineRunInstanceUuid);

    }

    @Override
    public PipelineRunRecord getPipelineRun(Long pipelineRunId) {
        return pipelineDb.getPipelineRun(pipelineRunId);
    }

    @Override
    public PipelineRunRecord getPipelineRunByInstanceUuid(String pipelineRunInstanceUuid) {
        return pipelineDb.getPipelineRunByInstanceUuid(pipelineRunInstanceUuid);
    }

    @Override
    public Set<String> listPipelineRunRunningAgent(Long userId) {
        Set<String> runningAgents = new HashSet<>();

        pipelineDb.listPipelineRun(userId, null, null, null,
                null, true, true,null, null, null, null
        ).stream().forEach(pipelineRunRecord -> {
            PipelineRun pipelineRun = pipelineRunRecord.toPojo();
            List<PipelineStep> steps = pipelineRun.getPipeline().getSteps();
            if(null != steps){
                steps.stream()
                        .filter(step -> EnumStepRunType.REMOTE_PREPARE_ENV_TYPE.equals(step.getType()))
                        .forEach(step -> {
                            Map<String, Object> params = step.getParams();
                            if(null != params){
                                String agentId = (String)params.get("agentId");
                                if(!StringUtil.isBlank(agentId)){
                                    runningAgents.add(agentId);
                                }
                            }
                        });
            }
        });

        return runningAgents;
    }

    @Override
    public boolean updatePipelineRunStatus(Long pipelineRunId, String status, String error, Map<String, String> outParams) {
        PipelineRunRecord pipelineRunRecord = pipelineDb.getPipelineRun(pipelineRunId);
        PipelineRun pipelineRun = pipelineRunRecord.toPojo();
        pipelineRun.setStatus(status);
        pipelineRun.setError(error);
        pipelineRun.setOutParams(outParams);

        Date now = new Date();

        switch (status){
            case EnumPipelineRunStatus.STATUS_STARTED:
                pipelineRun.setBeginTime(now);
                break;
            case EnumPipelineRunStatus.STATUS_FAILED:
                if(null == pipelineRun.getBeginTime()){
                    pipelineRun.setBeginTime(now);
                }
            case EnumPipelineRunStatus.STATUS_PASSED:
                pipelineRun.setEndTime(now);
                break;
        }

        pipelineRunRecord = pipelineRun.toRecord();

        return pipelineDb.updatePipelineRun(pipelineRunRecord);
    }

    @Override
    public boolean deletePipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId) {
        PipelineRunRecord pipelineRunRecord = pipelineDb.getPipelineRun(userId, projectId, pipelineId, pipelineRunId);
        if(null == pipelineRunRecord){
            return false;
        }

        pipelineRunRecord.setDeleted(true);
        pipelineRunRecord.setDeleteTime(new Date());

        boolean updated = pipelineDb.updatePipelineRun(pipelineRunRecord);
        if(!updated){
            return false;
        }

        return true;
    }

    @Override
    public List<PipelineStepRunRecord> listPipelineStepRun(Long pipelineRunId) {
        return pipelineDb.listPipelineStepRun(pipelineRunId);
    }

    @Override
    public PipelineRunDetailVo getPipelineRunVo(Long userId, Long projectId, Long pipelineId, Long pipelineRunId) {
        PipelineRunDetailVo vo = new PipelineRunDetailVo();

        PipelineRunRecord runRecord = pipelineDb.getPipelineRun(userId, projectId, pipelineId, pipelineRunId);
        if(null == runRecord){
            return null;
        }

        // replace password in input parameters
        PipelineRun pipelineRun = runRecord.toPojo();
        vo.setPipelineRun(pipelineRun);

        List<PipelineStepRunRecord> stepRuns = pipelineDb.listPipelineStepRun(pipelineRunId);
        vo.setPipelineStepRuns(stepRuns);

        return vo;
    }

    @Override
    public PipelineRunDetailVo getPipelineRunVoDesensitized(Long userId, Long projectId, Long pipelineId, Long pipelineRunId) {
        PipelineRunDetailVo vo = new PipelineRunDetailVo();

        PipelineRunRecord runRecord = pipelineDb.getPipelineRun(userId, projectId, pipelineId, pipelineRunId);
        if(null == runRecord){
            return null;
        }

        // order and replace password in input parameters
        PipelineRun pipelineRun = runRecord.toPojo();
        Pipeline pipeline = pipelineRun.getPipeline();
        Map<String, PipelineVariable> variables = pipeline.getVariables();
        Map<String, String> inParams = pipelineRun.getInParams();
        Map<String, String> inParamsOrdered = new LinkedHashMap<>();
        if(null != variables && null != inParams){
            inParams.keySet().stream().sorted((a, b) -> {
                int orderA = 0;
                int orderB = 0;

                PipelineVariable va = variables.get(a);
                PipelineVariable vb = variables.get(b);

                if(null != va){
                    Integer oa = va.getOrder();
                    if(null != oa){
                        orderA = oa;
                    }
                }

                if(null != vb){
                    Integer ob = vb.getOrder();
                    if(null != ob){
                        orderB = ob;
                    }
                }

                return orderA - orderB;
            }).forEach(name -> {
                String value = inParams.get(name);

                PipelineVariable variable = variables.get(name);
                if(null != variable){
                    Boolean password = variable.getPassword();
                    if(null != password && password){
                        value = PipelineVariableVo.SECRET_MASK;
                    }
                }

                inParamsOrdered.put(name, value);
            });
        }

        pipelineRun.setInParams(inParamsOrdered);
        vo.setPipelineRun(pipelineRun);

        List<PipelineStepRunRecord> stepRuns = pipelineDb.listPipelineStepRun(pipelineRunId);
        vo.setPipelineStepRuns(stepRuns);

        return vo;
    }

    @Override
    public List<PipelineRunRecord> listPipelineRun(
            Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning, Boolean noPseudo,
            String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return pipelineDb.listPipelineRun(userId, projectId, pipelineId, name, status, onlyRunning, noPseudo, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long countPipelineRun(Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning, Boolean noPseudo) {
        return pipelineDb.countPipelineRun(userId, projectId, pipelineId, name, status, onlyRunning, noPseudo);
    }

    @Override
    public long countPipelineRun(Long userId, Date beginTime, Date endTime) {
        return pipelineDb.countPipelineRun(userId, beginTime, endTime);
    }

    @Override
    public boolean updatePipelineStepRunStatus(Long pipelineRunId, int position, String status, String error) {
        PipelineStepRunRecord stepRunRecord = pipelineDb.getPipelineStepRun(pipelineRunId, position);
        stepRunRecord.setStatus(status);
        switch (status){
            case EnumPipelineStepRunStatus.STATUS_STARTED:
                stepRunRecord.setBeginTime(new Date());
                break;
            case EnumPipelineStepRunStatus.STATUS_SKIPPED:
                stepRunRecord.setBeginTime(new Date());
            case EnumPipelineStepRunStatus.STATUS_FAILED:
            case EnumPipelineStepRunStatus.STATUS_PASSED:
                stepRunRecord.setEndTime(new Date());
                stepRunRecord.setError(error);
                break;
        }

        return pipelineDb.updatePipelineStepRun(stepRunRecord);
    }

    @Override
    public boolean createVariable(Long userId, Long projectId, Long pipelineId, PipelineVariable variable) {
        PipelineRecord record = pipelineDb.get(userId, projectId, pipelineId);
        if(null == record){
            throw new RuntimeException("no pipeline");
        }

        Pipeline pipeline = record.toPojo();
        Map<String, PipelineVariable> variables = pipeline.getVariables();
        if(null == variables){
            variables = new HashMap<>();
            pipeline.setVariables(variables);
        }

        String name = variable.getName();
        if(variables.containsKey(name)){
            throw new RuntimeException("variable already exists");
        }

        if(EnumPipelineVariableKind.SECRET.equals(variable.getKind())){
            try {
                String value = AesUtil.encryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(),variable.getValue());
                variable.setValue(value);
                variables.put(name, variable);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            variables.put(name, variable);
        }

        return pipelineDb.update(pipeline.toRecord());
    }

    @Override
    public boolean updateVariable(Long userId, Long projectId, Long pipelineId, PipelineVariable variable) {
        PipelineRecord record = pipelineDb.get(userId, projectId, pipelineId);
        if(null == record){
            throw new RuntimeException("no pipeline");
        }

        Pipeline pipeline = record.toPojo();
        Map<String, PipelineVariable> variables = pipeline.getVariables();
        if(null == variables){
            variables = new HashMap<>();
            pipeline.setVariables(variables);
        }

        String name = variable.getName();
        if(!variables.containsKey(name)){
            throw new RuntimeException("no variable");
        }

        if(EnumPipelineVariableKind.SECRET.equals(variable.getKind())){
            try {
                String value = variable.getValue();
                if(!PipelineVariableVo.SECRET_MASK.equals(value)) {
                    value = AesUtil.encryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(), value);
                }else{
                    value = variables.get(name).getValue();
                }
                variable.setValue(value);
                variables.put(name, variable);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            variables.put(name, variable);
        }

        return pipelineDb.update(pipeline.toRecord());
    }

    @Override
    public boolean deleteVariable(Long userId, Long projectId, Long pipelineId, String variableName) {
        PipelineRecord record = pipelineDb.get(userId, projectId, pipelineId);
        if(null == record){
            throw new RuntimeException("no pipeline");
        }

        Pipeline pipeline = record.toPojo();
        Map<String, PipelineVariable> variables = pipeline.getVariables();
        if(null == variables){
            variables = new HashMap<>();
            pipeline.setVariables(variables);
        }

        if(!variables.containsKey(variableName)){
            throw new RuntimeException("no variable");
        }

        variables.remove(variableName);

        return pipelineDb.update(pipeline.toRecord());
    }

    @Override
    public PipelineWebhookTriggerRecord createPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String sourceType, String eventType, String tag, Map<String, String> inParams) {
        if(null == inParams){
            inParams = new HashMap<>();
        }

        PipelineWebhookTrigger trigger = new PipelineWebhookTrigger();
        trigger.setUserId(userId);
        trigger.setProjectId(projectId);
        trigger.setPipelineId(pipelineId);
        trigger.setSourceType(sourceType);
        trigger.setEventType(eventType);
        trigger.setTag(tag);
        trigger.setInParams(inParams);
        trigger.setCreateTime(new Date());

        PipelineWebhookTriggerRecord triggerRecord = trigger.toRecord();
        boolean created = pipelineDb.createPipelineWebhookTrigger(triggerRecord);
        if(!created){
            return null;
        }

        return triggerRecord;
    }

    @Override
    public boolean updatePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId, String sourceType, String eventType, String tag, Map<String, String> inParams) {
        PipelineWebhookTriggerRecord triggerRecord = pipelineDb.getPipelineWebhookTrigger(
                userId, projectId, pipelineId, pipelineWebhookTriggerId);
        if(null == triggerRecord){
            return false;
        }

        PipelineWebhookTrigger trigger = triggerRecord.toPojo();

        trigger.setSourceType(sourceType);
        trigger.setEventType(eventType);
        trigger.setTag(tag);
        trigger.setInParams(inParams);

        boolean updated = pipelineDb.updatePipelineWebhookTrigger(trigger.toRecord());
        return updated;
    }

    @Override
    public boolean updatePipelineWebhookTriggerLastTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId, Date lastTriggerTime, Long lastTriggerPipelineRunId) {
        PipelineWebhookTriggerRecord triggerRecord = pipelineDb.getPipelineWebhookTrigger(
                userId, projectId, pipelineId, pipelineWebhookTriggerId);
        if(null == triggerRecord){
            return false;
        }

        PipelineWebhookTrigger trigger = triggerRecord.toPojo();

        trigger.setLastTriggerTime(lastTriggerTime);
        trigger.setLastTriggerPipelineRunId(lastTriggerPipelineRunId);

        boolean updated = pipelineDb.updatePipelineWebhookTrigger(trigger.toRecord());
        return updated;
    }

    @Override
    public PipelineWebhookTriggerRecord getPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId) {
        PipelineWebhookTriggerRecord triggerRecord = pipelineDb.getPipelineWebhookTrigger(
                userId, projectId, pipelineId, pipelineWebhookTriggerId);
        return triggerRecord;
    }

    @Override
    public List<PipelineWebhookTriggerRecord> listPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        List<PipelineWebhookTriggerRecord> list = pipelineDb.listPipelineWebhookTrigger(
                userId, projectId, pipelineId, orderField, orderType, pageNo, pageSize);
        return list;
    }

    @Override
    public long countPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId) {
        long count = pipelineDb.countPipelineWebhookTrigger(userId, projectId, pipelineId);
        return count;
    }

    @Override
    public boolean deletePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId) {
        return pipelineDb.deletePipelineWebhookTrigger(userId, projectId, pipelineId, pipelineWebhookTriggerId);
    }

    @Override
    public PipelinePendingRecord createPipelinePending(Long userId, Long projectId, Long pipelineId, String name, Map<String, String> inParams, Boolean flagDebug, String createReason) {
        if(null == inParams){
            inParams = new HashMap<>();
        }

        PipelinePending pending = new PipelinePending();
        pending.setUserId(userId);
        pending.setProjectId(projectId);
        pending.setPipelineId(pipelineId);
        pending.setName(name);
        pending.setInParams(inParams);
        pending.setFlagDebug(flagDebug);
        pending.setCreateReason(createReason);
        pending.setCreateTime(new Date());

        PipelinePendingRecord pendingRecord = pending.toRecord();
        boolean created = pipelineDb.createPipelinePending(pendingRecord);
        if(!created){
            return null;
        }

        return pendingRecord;
    }

    @Override
    public PipelinePendingRecord getPipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId) {
        PipelinePendingRecord pipelinePendingRecord = pipelineDb.getPipelinePending(userId, projectId, pipelineId, pipelinePendingId);
        return pipelinePendingRecord;
    }

    @Override
    public PipelinePendingRecord getEarliestPipelinePending(Long userId, Long projectId, Long pipelineId) {
        PipelinePendingRecord pipelinePendingRecord = pipelineDb.getEarliestPipelinePending(userId, projectId, pipelineId);
        return pipelinePendingRecord;
    }

    @Override
    public List<PipelinePendingRecord> listPipelinePending(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        List<PipelinePendingRecord> list = pipelineDb.listPipelinePending(userId, projectId, pipelineId, orderField, orderType, pageNo, pageSize);
        return list;
    }

    @Override
    public long countPipelinePending(Long userId, Long projectId, Long pipelineId) {
        long count = pipelineDb.countPipelinePending(userId, projectId, pipelineId);
        return count;
    }

    @Override
    public boolean deletePipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId) {
        boolean deleted = pipelineDb.deletePipelinePending(userId, projectId, pipelineId, pipelinePendingId);
        return deleted;
    }

}
