package com.airxiechao.y20.pipeline.run.pipeline;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.process.statemachine.AbstractStateMachine;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.function.QuadConsumer;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.db.record.PipelineStepRunRecord;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.*;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.pubsub.event.explorer.EventExplorerRunTransferProgress;
import com.airxiechao.y20.pipeline.pubsub.event.step.EventStepRunOutputPush;
import com.airxiechao.y20.pipeline.pubsub.event.terminal.EventTerminalRunOutputPush;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunTransferProgressListener;
import com.airxiechao.y20.pipeline.run.explorer.RemoteExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstanceFactory;
import com.airxiechao.y20.pipeline.run.step.param.RemoteDestroyEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemotePrepareEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemoteStartEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemoteStopEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.terminal.ITerminalRunInstance;
import com.airxiechao.y20.pipeline.run.terminal.RemoteTerminalRunInstance;
import com.airxiechao.y20.pipeline.run.util.PipelineStepRunUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PipelineRunInstance extends AbstractStateMachine implements IPipelineRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunInstance.class);

    private static final String RESP_CODE_STEP_SKIPPED = "RESP_CODE_STEP_SKIPPED";
    public static final String STATE_STEP_WAITING = "STEP_WAITING";
    public static final String STATE_STEP_ITERATING = "STEP_ITERATING";
    public static final String STATE_STEP_RUNNING = "STEP_RUNNING";
    public static final String STATE_INTERRUPTED = "INTERRUPTED";
    public static final String STATE_FINISHED = "FINISHED";

    private PipelineRunDetailVo pipelineRunDetail;
    private String pipelineRunInstanceUuid;
    private ThreadPoolExecutor executor;

    private boolean flagIterate = false;
    private boolean flagStop = false;
    private String error;
    private StepIterator stepIterator;

    private Env currentEnv;
    private AbstractPipelineRunContext pipelineRunContext;
    private Map<String, String> pipelineRunOutParams = new LinkedHashMap<>();

    private StepRunContainer stepRunContainer = new StepRunContainer();
    private TerminalRunContainer currentEnvTerminalRunContainer = this.new TerminalRunContainer();
    private ExplorerRunContainer currentEnvExplorerRunContainer = this.new ExplorerRunContainer();

    private List<QuadConsumer<String, String, String, Map<String, String>>> pipelineRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<QuadConsumer<Integer, String, String, String>> stepRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<Integer, String>> stepRunOutputListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<String, String>> terminalRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<String, String>> terminalRunOutputListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<String, String>> explorerRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<IExplorerRunTransferProgressListener> explorerRunTransferProgressListeners = Collections.synchronizedList(new ArrayList<>());

    public PipelineRunInstance(
            PipelineRunDetailVo pipelineRunDetail,
            ThreadPoolExecutor executor
    ) {
        this.pipelineRunDetail = pipelineRunDetail;
        this.pipelineRunInstanceUuid = pipelineRunDetail.getPipelineRun().getInstanceUuid();
        this.executor = executor;
    }

    @Override
    protected void initStm() {

        /*
                                          +--------------------------------> FINISHED
                                          |
                                          +----------------------------+---> INTERRUPTED
                                          |                            |
        INIT ---> STEP_WAITING ---> STEP_ITERATING --> STEP_RUNNING ---|
                       ^                  ^                   |
                       |                  |                   |
                       |                  |-------------------+
                       |                                      |
                       |--------------------------------------|

         */

        stm.addPath(STATE_INIT, STATE_STEP_WAITING);
        stm.addPath(STATE_STEP_WAITING, STATE_STEP_ITERATING);
        stm.addPath(STATE_STEP_ITERATING, STATE_STEP_RUNNING);
        stm.addPath(STATE_STEP_ITERATING, STATE_INTERRUPTED);
        stm.addPath(STATE_STEP_ITERATING, STATE_FINISHED);

        stm.addPath(STATE_STEP_RUNNING, STATE_INTERRUPTED);
        stm.addPath(STATE_STEP_RUNNING, STATE_STEP_WAITING);
        stm.addPath(STATE_STEP_RUNNING, STATE_STEP_ITERATING);

        stm.addPath(STATE_FINISHED, STATE_STEP_WAITING);
        stm.addPath(STATE_INTERRUPTED, STATE_STEP_WAITING);
    }

    @Override
    protected void transferToState(String state) {
        switch (state){
            case STATE_STEP_WAITING:

                this.pipelineRunStatusUpdateListeners.forEach(listener -> {
                    listener.accept(pipelineRunInstanceUuid, EnumPipelineRunStatus.STATUS_WAITING, null, null);
                });

                if(!flagIterate){
                    enterState(STATE_STEP_ITERATING);
                }
                break;
            case STATE_STEP_ITERATING:
                if(this.stepIterator.hasNext()){
                    if(this.flagStop){
                        error = "stop by flag";
                        enterState(STATE_INTERRUPTED);
                    }else{
                        enterState(STATE_STEP_RUNNING);
                    }
                }else{
                    enterState(STATE_FINISHED);
                }
                break;
            case STATE_STEP_RUNNING:
                PipelineStepRunRecord stepRunRecord;
                try {
                    stepRunRecord = this.stepIterator.next();
                }catch (Exception e){
                    logger.error("pipeline run [{}] step iteration error", pipelineRunInstanceUuid, e);
                    error = e.getMessage();
                    enterState(STATE_INTERRUPTED);
                    break;
                }

                int position = this.stepIterator.getPosition();
                int total = this.stepIterator.getTotal();

                executeStep(stepRunRecord, stepRunInstanceUuid -> {
                    // when start
                    logger.info("pipeline run [{}] step {}-{} [{}] start", pipelineRunInstanceUuid, position+1, total, stepRunRecord.getType());

                    this.pipelineRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(pipelineRunInstanceUuid, EnumPipelineRunStatus.STATUS_RUNNING, null, null);
                    });

                    this.stepRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(position, stepRunInstanceUuid, EnumPipelineStepRunStatus.STATUS_STARTED, null);
                    });
                }, (stepRunInstanceUuid, response) -> {
                    // when response
                    String status;
                    String message = response.getMessage();
                    String respCode = response.getCode();
                    switch (respCode){
                        case Response.CODE_OK:
                            logger.info("pipeline run [{}] step {}-{} [{}] run -> {}, {}", pipelineRunInstanceUuid, position+1, total, stepRunRecord.getType(), true, response.getData());
                            status = EnumPipelineStepRunStatus.STATUS_PASSED;
                            break;
                        case RESP_CODE_STEP_SKIPPED:
                            logger.error("pipeline run [{}] step {}-{} [{}] run skipped: {}", pipelineRunInstanceUuid, position+1, total, stepRunRecord.getType(), response.getMessage());
                            status = EnumPipelineStepRunStatus.STATUS_SKIPPED;
                            break;
                        default:
                            logger.error("pipeline run [{}] step {}-{} [{}] run error: {}", pipelineRunInstanceUuid, position+1, total, stepRunRecord.getType(), response.getMessage());
                            status = EnumPipelineStepRunStatus.STATUS_FAILED;
                            break;
                    }

                    this.stepRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(position, stepRunInstanceUuid, status, message);
                    });

                    switch (respCode) {
                        case Response.CODE_OK:
                        case RESP_CODE_STEP_SKIPPED:
                            if(flagIterate){
                                enterState(STATE_STEP_WAITING);
                            }else{
                                enterState(STATE_STEP_ITERATING);
                            }
                            break;
                        default:
                            error = message;
                            enterState(STATE_INTERRUPTED);
                            break;
                    }
                });
                break;
            case STATE_INTERRUPTED:
                executeStepsOver(false);
                break;
            case STATE_FINISHED:
                executeStepsOver(true);
                break;
        }
    }

    @Override
    public void start() {
        switch (state){
            case STATE_INIT:
                logger.info("▲ pipeline run [{}] start", this.pipelineRunInstanceUuid);

                executeStepsStart(false);

                break;
        }
    }

    @Override
    public CompletableFuture startAsync() {
        logger.info("pipeline run [uuid:{}] start async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                pipelineRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture future = CompletableFuture.runAsync(() -> start(), executor);
        return future;
    }

    @Override
    public void iterateStep() {
        switch (state){
            case STATE_INIT:
                logger.info("▲ pipeline run [{}] iterate", this.pipelineRunInstanceUuid);

                executeStepsStart(true);

                break;
        }
    }

    @Override
    public CompletableFuture iterateStepAsync() {
        logger.info("pipeline run [uuid:{}] iterate step async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                pipelineRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture future = CompletableFuture.runAsync(() -> iterateStep(), executor);
        return future;
    }

    @Override
    public void nextStep() {
        if(!flagIterate){
            return;
        }

        switch (state) {
            case STATE_STEP_WAITING:
                logger.info("▲ pipeline run [{}] next step", this.pipelineRunInstanceUuid);
                enterState(STATE_STEP_ITERATING);
                break;
        }
    }

    @Override
    public CompletableFuture nextStepAsync() {
        logger.info("pipeline run [uuid:{}] next step async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                pipelineRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture future = CompletableFuture.runAsync(() -> nextStep(), executor);
        return future;
    }

    @Override
    public void stop() {
        logger.info("● pipeline run [{}] stop", this.pipelineRunInstanceUuid);

        this.flagStop = true;

        switch (state){
            case STATE_STEP_WAITING:
                if(flagIterate) {
                    enterState(STATE_STEP_ITERATING);
                }
                break;
            case STATE_STEP_RUNNING:
                stepRunContainer.stopLastStepRun();
                break;
        }

    }

    @Override
    public CompletableFuture stopAsync() {
        logger.info("pipeline run [uuid:{}] stop async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                pipelineRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture future = CompletableFuture.runAsync(() -> stop(), executor);
        return future;
    }

    @Override
    public void stepRunInstanceCallback(String stepRunInstanceUuid, Response response) {
        stepRunContainer.executeStepRunCallback(stepRunInstanceUuid, response);
    }

    @Override
    public String getPipelineRunInstanceUuid(){
        return this.pipelineRunInstanceUuid;
    }

    @Override
    public IExplorerRunInstance getExplorerRunInstance(String explorerRunInstanceUuid) {
        return getCurrentEnvExplorerRunContainer().get(explorerRunInstanceUuid);
    }

    @Override
    public IExplorerRunInstance createExplorerRunInstance() throws Exception {
        return getCurrentEnvExplorerRunContainer().create();
    }

    @Override
    public void destroyExplorerRunInstance(String explorerRunInstanceUuid) throws Exception {
        getCurrentEnvExplorerRunContainer().destroy(explorerRunInstanceUuid);
    }

    @Override
    public void addExplorerRunStatusUpdateListener(BiConsumer<String, String> explorerRunStatusUpdateListener) {
        this.explorerRunStatusUpdateListeners.add(explorerRunStatusUpdateListener);
    }

    @Override
    public void removeExplorerRunStatusUpdateListener(BiConsumer<String, String> explorerRunStatusUpdateListener) {
        this.explorerRunStatusUpdateListeners.remove(explorerRunStatusUpdateListener);
    }

    @Override
    public void addExplorerRunTransferProgressListener(IExplorerRunTransferProgressListener explorerRunTransferProgressListener) {
        this.explorerRunTransferProgressListeners.add(explorerRunTransferProgressListener);
    }

    @Override
    public void removeExplorerRunTransferProgressListener(IExplorerRunTransferProgressListener explorerRunTransferProgressListener) {
        this.explorerRunTransferProgressListeners.remove(explorerRunTransferProgressListener);
    }

    @Override
    public ITerminalRunInstance getTerminalRunInstance(String terminalRunInstanceUuid) {
        return getCurrentEnvTerminalRunContainer().get(terminalRunInstanceUuid);
    }

    @Override
    public ITerminalRunInstance createTerminalRunInstance(String terminalType) throws Exception {
        return getCurrentEnvTerminalRunContainer().create(terminalType);
    }

    @Override
    public void destroyTerminalRunInstance(String terminalRunInstanceUuid) throws Exception {
        getCurrentEnvTerminalRunContainer().destroy(terminalRunInstanceUuid);
    }

    @Override
    public void addTerminalRunStatusUpdateListener(BiConsumer<String, String> terminalRunStatusUpdateListener) {
        this.terminalRunStatusUpdateListeners.add(terminalRunStatusUpdateListener);
    }

    @Override
    public void removeTerminalRunStatusUpdateListener(BiConsumer<String, String> terminalRunStatusUpdateListener) {
        this.terminalRunStatusUpdateListeners.remove(terminalRunStatusUpdateListener);
    }

    @Override
    public void addTerminalRunOutputListener(BiConsumer<String, String> terminalRunOutputListener) {
        this.terminalRunOutputListeners.add(terminalRunOutputListener);
    }

    @Override
    public void removeTerminalRunOutputListener(BiConsumer<String, String> terminalRunOutputListener) {
        this.terminalRunOutputListeners.remove(terminalRunOutputListener);
    }

    @Override
    public AbstractStepRunInstance getStepRunInstance(String stepRunId){
        return this.stepRunContainer.get(stepRunId);
    }

    @Override
    public void addStepRunOutputListener(BiConsumer<Integer, String> stepRunLogListener) {
        this.stepRunOutputListeners.add(stepRunLogListener);
    }

    @Override
    public void removeStepRunOutputListener(BiConsumer<Integer, String> stepRunLogListener) {
        this.stepRunOutputListeners.remove(stepRunLogListener);
    }

    @Override
    public void addStepRunStatusUpdateListener(QuadConsumer<Integer, String, String, String> stepRunStatusUpdateListener) {
        this.stepRunStatusUpdateListeners.add(stepRunStatusUpdateListener);
    }

    @Override
    public void removeStepRunStatusUpdateListener(QuadConsumer<Integer, String, String, String> stepRunStatusUpdateListener) {
        this.stepRunStatusUpdateListeners.remove(stepRunStatusUpdateListener);
    }

    @Override
    public boolean isStopped() {
        return state.equals(STATE_INTERRUPTED) || state.equals(STATE_FINISHED);
    }

    @Override
    public Env getCurrentEnv() {
        return this.currentEnv;
    }

    @Override
    public void addPipelineRunStatusUpdateListener(QuadConsumer<String, String, String, Map<String, String>> pipelineRunStatusUpdateListener) {
        this.pipelineRunStatusUpdateListeners.add(pipelineRunStatusUpdateListener);
    }

    @Override
    public void removePipelineRunStatusUpdateListener(QuadConsumer<String, String, String, Map<String, String>> pipelineRunStatusUpdateListener) {
        this.pipelineRunStatusUpdateListeners.remove(pipelineRunStatusUpdateListener);
    }

    private void init(boolean flagIterate){
        this.flagIterate = flagIterate;
        this.flagStop = false;
        this.error = null;

        this.stepRunContainer.clear();
        this.stepIterator = new StepIterator(pipelineRunDetail.getPipelineStepRuns());

        this.pipelineRunContext = new PipelineRunContext(pipelineRunDetail.getPipelineRun());
    }

    private void subscribeStepRunLogEvent(){
        String stepRunLogEventName = EventStepRunOutputPush.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().subscribe(
                stepRunLogEventName, pipelineRunInstanceUuid, map -> {
                    EventStepRunOutputPush event = ModelUtil.fromMap(map, EventStepRunOutputPush.class);
                    String stepRunInstanceUuid = event.getStepRunInstanceUuid();

                    AbstractStepRunInstance run = getStepRunInstance(stepRunInstanceUuid);
                    if(null == run){
                        return new Response().error("no step run");
                    }

                    String output = event.getOutput();
                    if(output.equals("\0")){
                        // end of log
                        run.finishLog();
                        return new Response();
                    }else{
                        // OutputStream logOutputStream = run.getLogOutputStream();
                        // OutputStreamWriter logWriter = new OutputStreamWriter(logOutputStream);
                        // logWriter.write(log);
                        // logWriter.flush();
                    }

                    this.stepRunOutputListeners.forEach(listener -> {
                        int position = stepIterator.getPositionByStepRunInstanceUuid(stepRunInstanceUuid);
                        listener.accept(position, output);
                    });
                    return new Response();
                }
        );
    }

    private void unsubscribeStepRunLogEvent(){
        String stepRunLogEventName = EventStepRunOutputPush.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().unsubscribe(stepRunLogEventName, this.pipelineRunInstanceUuid);
    }

    private void subscribeTerminalRunOutputEvent(){
        String terminalRunOutputEventName = EventTerminalRunOutputPush.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().subscribe(
                terminalRunOutputEventName, pipelineRunInstanceUuid, map -> {
                    EventTerminalRunOutputPush event = ModelUtil.fromMap(map, EventTerminalRunOutputPush.class);

                    String terminalRunInstanceUuid = event.getTerminalRunInstanceUuid();
                    ITerminalRunInstance run = getTerminalRunInstance(terminalRunInstanceUuid);
                    if(null == run){
                        return new Response().error("no terminal run");
                    }

                    String output = event.getOutput();

                    // OutputStream outputStream = run.getOutputStream();
                    // OutputStreamWriter logWriter = new OutputStreamWriter(outputStream);
                    // logWriter.write(output);
                    // logWriter.flush();

                    this.terminalRunOutputListeners.forEach(listener -> {
                        listener.accept(terminalRunInstanceUuid, output);
                    });
                    return new Response();
                }
        );
    }

    private void unsubscribeTerminalRunOutputEvent(){
        String terminalRunOutputEventName = EventTerminalRunOutputPush.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().unsubscribe(terminalRunOutputEventName, this.pipelineRunInstanceUuid);
    }

    private void subscribeExplorerRunTransferProgressEvent(){
        String explorerRunTransferProgressEventName = EventExplorerRunTransferProgress.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().subscribe(
                explorerRunTransferProgressEventName, pipelineRunInstanceUuid, map -> {
                    EventExplorerRunTransferProgress event = ModelUtil.fromMap(map, EventExplorerRunTransferProgress.class);

                    String explorerRunInstanceUuid = event.getExplorerRunInstanceUuid();
                    IExplorerRunInstance run = getExplorerRunInstance(explorerRunInstanceUuid);
                    if(null == run){
                        return new Response().error("no explorer run");
                    }

                    this.explorerRunTransferProgressListeners.forEach(listener -> {
                        listener.accept(explorerRunInstanceUuid,
                                event.getPath(),
                                event.getDirection(),
                                event.getTotal(),
                                event.getSpeed(),
                                event.getProgress()
                        );
                    });
                    return new Response();
                }
        );
    }

    private void unsubscribeExplorerRunTransferProgressEvent(){
        String explorerRunTransferProgressEventName = EventExplorerRunTransferProgress.type(this.pipelineRunInstanceUuid, "*");
        EventBus.getInstance().getPubSub().unsubscribe(explorerRunTransferProgressEventName, this.pipelineRunInstanceUuid);
    }

    private void executeStep(PipelineStepRunRecord stepRunRecord, Consumer<String> startConsumer, BiConsumer<String, Response> responseConsumer){
        PipelineStep step = pipelineRunDetail.getPipelineRun().getPipeline().getSteps().get(stepRunRecord.getPosition());
        Long userId = pipelineRunDetail.getPipelineRun().getUserId();
        String stepRunInstanceUuid = stepRunRecord.getInstanceUuid();

        // check condition
        String condition = step.getCondition();
        if(!StringUtil.isBlank(condition)){
            String conditionValue = pipelineRunContext.getVariableActualValue(condition);
            if(StringUtil.isBlank(conditionValue)){
                Response skipResp = new Response();
                skipResp.setCode(RESP_CODE_STEP_SKIPPED);
                skipResp.setMessage(String.format("condition %s not met", condition));
                responseConsumer.accept(null, skipResp);
                return;
            }
        }

        // create step run
        boolean isRemotePrepareEnvStep = step.getType().equals(EnumStepRunType.REMOTE_PREPARE_ENV_TYPE);
        AbstractStepRunInstance stepRun;
        try{
            Env env;
            if(isRemotePrepareEnvStep){
                env = new Env();
                env.setUserId(userId);
                env.setPipelineRunInstanceUuid(pipelineRunInstanceUuid);
            }else{
                env = currentEnv;
            }

            stepRun = RemoteStepRunInstanceFactory.getInstance().buildRemoteRun(
                    step, stepRunInstanceUuid, env, pipelineRunContext);
        }catch (Exception e){
            // create step run error
            responseConsumer.accept(null, new Response().error(e.getMessage()));
            return;
        }

        // add step run to container and register callback
        stepRunContainer.add(stepRun, resp -> {
            // step callback
            if(!resp.isSuccess()){
                responseConsumer.accept(stepRun.getStepRunInstanceUuid(), resp);
            }else{
                if(isRemotePrepareEnvStep){
                    // destroy previous env
                    destroyCurrentEnv(response -> {
                        // update current env
                        Env ret = ((JSONObject)resp.getData()).toJavaObject(Env.class);
                        RemotePrepareEnvStepRunParam param = ModelUtil.fromMap(step.getParams(), RemotePrepareEnvStepRunParam.class);
                        Env env = new Env(
                                userId,
                                pipelineRunDetail.getPipelineRun().getProjectId(),
                                pipelineRunDetail.getPipelineRun().getPipelineId(),
                                pipelineRunDetail.getPipelineRun().getPipelineRunId(),
                                pipelineRunInstanceUuid,
                                param.getAgentId(),
                                param.getImage(),
                                ret.getDockerHostWorkingDir(),
                                ret.getWorkingDir(),
                                ret.getDockerContainerId(),
                                ret.getEnvInstanceUuid());
                        this.currentEnv = env;

                        // start current env
                        startRemoteEnv(currentEnv, response2 -> {
                            responseConsumer.accept(stepRun.getStepRunInstanceUuid(), response2);
                        });
                    });
                }else{
                    // update context variable
                    Object ret = resp.getData();
                    if(ret instanceof Map){
                        Map<String, Object> retMap = (Map<String, Object>)ret;
                        retMap.entrySet().forEach((entry) -> {
                            String key = entry.getKey();
                            String value = entry.getValue().toString();
                            pipelineRunContext.setVariableValue(key, value);
                            pipelineRunOutParams.put(key, value);
                        });
                    }

                    responseConsumer.accept(stepRun.getStepRunInstanceUuid(), resp);
                }
            }
        });

        // start step run
        startConsumer.accept(stepRun.getStepRunInstanceUuid());

        try{
            stepRun.startAsync(e -> {
                // run exception
                responseConsumer.accept(stepRun.getStepRunInstanceUuid(), new Response().error(e.getMessage()));
            }, executor);
        }catch (Exception e){
            // start error
            responseConsumer.accept(stepRun.getStepRunInstanceUuid(), new Response().error(e.getMessage()));
        }
    }

    private void executeStepsStart(boolean flagIterate){
        init(flagIterate);

        this.pipelineRunStatusUpdateListeners.forEach(listener -> {
            listener.accept(pipelineRunInstanceUuid, EnumPipelineRunStatus.STATUS_STARTED, null, null);
        });

        this.subscribeStepRunLogEvent();
        this.subscribeTerminalRunOutputEvent();
        this.subscribeExplorerRunTransferProgressEvent();

        enterState(STATE_STEP_WAITING);
    }

    private void executeStepsOver(boolean passed){
        destroyCurrentEnv(resp -> {
            this.pipelineRunStatusUpdateListeners.forEach(listener -> {
                listener.accept(
                        pipelineRunInstanceUuid,
                        passed ? EnumPipelineRunStatus.STATUS_PASSED : EnumPipelineRunStatus.STATUS_FAILED,
                        error,
                        pipelineRunOutParams
                );
            });

            this.unsubscribeStepRunLogEvent();
            this.unsubscribeTerminalRunOutputEvent();
            this.unsubscribeExplorerRunTransferProgressEvent();

            logger.info("● pipeline run [{}] execute -> {}", this.pipelineRunInstanceUuid, passed);
        });
    }

    private void destroyCurrentEnv(Consumer<Response> responseConsumer){
        if(null != currentEnv){
            this.currentEnvTerminalRunContainer.clear();
            this.currentEnvExplorerRunContainer.clear();

            Env env = ModelUtil.deepCopy(currentEnv, Env.class);
            stopRemoteEnv(env, resp -> {
                if(!resp.isSuccess()){
                    logger.error("stop env error: {}", resp.getMessage());
                    responseConsumer.accept(resp);
                    return;
                }

                destroyRemoteEnv(env, resp2 -> {
                    if(!resp2.isSuccess()){
                        logger.error("destroy env error: {}", resp2.getMessage());
                        responseConsumer.accept(resp2);
                        return;
                    }

                    responseConsumer.accept(new Response());
                });
            });

            currentEnv = null;
        }else{
            responseConsumer.accept(new Response());
        }
    }

    private void startRemoteEnv(Env env, Consumer<Response> responseConsumer) {
        logger.info("start remote env [containerId:{}]", env.getDockerContainerId());

        PipelineStep step = new PipelineStep();
        step.setType(EnumStepRunType.REMOTE_START_ENV_TYPE);
        step.setParams(ModelUtil.toMap(new RemoteStartEnvStepRunParam()));
        var remoteStepRun = RemoteStepRunInstanceFactory.getInstance().buildRemoteRun(
                step, null, env, pipelineRunContext);

        stepRunContainer.add(remoteStepRun, response -> {
            stepRunContainer.remove(remoteStepRun);

            logger.info("start remote env [containerId:{}] -> {}", env.getDockerContainerId(), response.isSuccess());
            responseConsumer.accept(response);
        });
        remoteStepRun.startAsync(e -> {
            responseConsumer.accept(new Response().error(e.getMessage()));
        }, executor);
    }

    private void stopRemoteEnv(Env env, Consumer<Response> responseConsumer) {
        logger.info("stop remote env [containerId:{}]", env.getDockerContainerId());

        PipelineStep step = new PipelineStep();
        step.setType(EnumStepRunType.REMOTE_STOP_ENV_TYPE);
        step.setParams(ModelUtil.toMap(new RemoteStopEnvStepRunParam()));
        var remoteStepRun = RemoteStepRunInstanceFactory.getInstance().buildRemoteRun(
                step, null, env, pipelineRunContext);

        stepRunContainer.add(remoteStepRun, response -> {
            stepRunContainer.remove(remoteStepRun);
            logger.info("stop remote env [containerId:{}] -> {}", env.getDockerContainerId(), response.isSuccess());

            responseConsumer.accept(response);
        });

        remoteStepRun.startAsync(e -> {
            logger.error("stop remote env [containerId:{}] error", env.getDockerContainerId(), e);
            responseConsumer.accept(new Response().error(e.getMessage()));
        }, executor);
    }

    private void destroyRemoteEnv(Env env, Consumer<Response> responseConsumer) {
        logger.info("destroy remote env [containerId:{}]", env.getDockerContainerId());

        PipelineStep step = new PipelineStep();
        step.setType(EnumStepRunType.REMOTE_DESTROY_ENV_TYPE);
        step.setParams(ModelUtil.toMap(new RemoteDestroyEnvStepRunParam()));
        var remoteStepRun = RemoteStepRunInstanceFactory.getInstance().buildRemoteRun(
                step, null, env, pipelineRunContext);

        stepRunContainer.add(remoteStepRun, response -> {
            stepRunContainer.remove(remoteStepRun);
            logger.info("destroy remote env [containerId:{}] -> {}", env.getDockerContainerId(), response.isSuccess());

            responseConsumer.accept(response);
        });
        remoteStepRun.startAsync(e -> {
            logger.error("destroy remote env [containerId:{}] error", env.getDockerContainerId(), e);
            responseConsumer.accept(new Response().error(e.getMessage()));
        }, executor);
    }

    /******************************************************************************************
     *
     *                               step iterator mixin
     *
     ******************************************************************************************/

    public class StepIterator implements Iterator<PipelineStepRunRecord>{

        private List<PipelineStepRunRecord> steps;
        private Iterator<PipelineStepRunRecord> iterator;
        private int position;
        private int total;

        public StepIterator(List<PipelineStepRunRecord> steps){
            this.steps = steps;
            this.iterator = steps.iterator();
            this.position = -1;
            this.total = steps.size();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public PipelineStepRunRecord next() {
            this.position += 1;
            PipelineStepRunRecord stepRunRecord = iterator.next();
            PipelineStep step = pipelineRunDetail.getPipelineRun().getPipeline().getSteps().get(stepRunRecord.getPosition());

            // replace param holder by variable
            PipelineStepRunUtil.replaceHolderByContext(step.getParams(), pipelineRunContext);

            return stepRunRecord;
        }

        public int getPosition() {
            return position;
        }

        public int getPositionByStepRunInstanceUuid(String stepRunInstanceUuid){
            for(int i = 0; i < steps.size(); ++i){
                PipelineStepRunRecord step  = steps.get(i);
                if(stepRunInstanceUuid.equals(step.getInstanceUuid())){
                    return i;
                }
            }

            return -1;
        }

        public int getTotal() {
            return total;
        }
    }

    /******************************************************************************************
     *
     *                               step run container mixin
     *
     ******************************************************************************************/

    public class StepRunContainer {
        private Map<String, AbstractStepRunInstance> stepRunMap = Collections.synchronizedMap(new LinkedHashMap<>());
        private Map<String, Consumer<Response>> stepRunCallbackMap = new ConcurrentHashMap<>();

        public AbstractStepRunInstance get(String stepRunId){
            return this.stepRunMap.get(stepRunId);
        }

        public void add(AbstractStepRunInstance stepRun){
            this.stepRunMap.put(stepRun.getStepRunInstanceUuid(), stepRun);
        }

        public void add(AbstractStepRunInstance stepRun, Consumer<Response> callback){
            this.stepRunMap.put(stepRun.getStepRunInstanceUuid(), stepRun);
            this.stepRunCallbackMap.put(stepRun.getStepRunInstanceUuid(), callback);
        }

        public void remove(AbstractStepRunInstance stepRun){
            this.stepRunMap.remove(stepRun.getStepRunInstanceUuid());
            this.stepRunCallbackMap.remove(stepRun.getStepRunInstanceUuid());
        }

        public void clear(){
            this.stepRunMap.clear();
            this.stepRunCallbackMap.clear();
        }

        public Response stopLastStepRun(){

            List<AbstractStepRunInstance> list = new ArrayList<>(this.stepRunMap.values());
            if(list.size() == 0){
                return new Response();
            }

            AbstractStepRunInstance run = list.get(list.size() - 1);
            try {
                return run.stop();
            } catch (Exception e) {
                return new Response().error(e.getMessage());
            }
        }

        public void executeStepRunCallback(String stepRunId, Response response){
            Consumer<Response> callback = this.stepRunCallbackMap.get(stepRunId);
            if(null != callback){
                callback.accept(response);
            }
        }
    }

    public StepRunContainer getStepRunContainer() {
        return stepRunContainer;
    }

    /******************************************************************************************
     *
     *                               terminal run container mixin
     *
     ******************************************************************************************/

    public class TerminalRunContainer {
        private Map<String, ITerminalRunInstance> terminalRunMap = new ConcurrentHashMap<>();

        public ITerminalRunInstance create(String terminalType) throws Exception {
            ITerminalRunInstance terminalRunInstance = new RemoteTerminalRunInstance(
                    pipelineRunInstanceUuid,
                    terminalType,
                    currentEnv);

            terminalRunInstance.create();

            this.terminalRunMap.put(terminalRunInstance.getTerminalRunInstanceUuid(), terminalRunInstance);

            terminalRunStatusUpdateListeners.forEach(listener -> {
                listener.accept(terminalRunInstance.getTerminalRunInstanceUuid(), EnumTerminalRunStatus.STATUS_STARTED);

            });

            return terminalRunInstance;
        }

        public void destroy(String terminalRunInstanceUuid) throws Exception {
            ITerminalRunInstance terminalRun = this.terminalRunMap.get(terminalRunInstanceUuid);
            if(null != terminalRun){
                terminalRun.destroy();
                this.terminalRunMap.remove(terminalRunInstanceUuid);

                terminalRunStatusUpdateListeners.forEach(listener -> {
                    listener.accept(terminalRun.getTerminalRunInstanceUuid(), EnumTerminalRunStatus.STATUS_STOPPED);
                });
            }
        }

        public void clear(){
            for(String terminalRunInstanceUuid : this.terminalRunMap.keySet()){
                try {
                    destroy(terminalRunInstanceUuid);
                } catch (Exception e) {
                    logger.error("destroy terminal run error", e);
                }
            }
        }

        public ITerminalRunInstance get(String terminalRunInstanceUuid){
            return this.terminalRunMap.get(terminalRunInstanceUuid);
        }

    }

    public TerminalRunContainer getCurrentEnvTerminalRunContainer() {
        return currentEnvTerminalRunContainer;
    }

    /******************************************************************************************
     *
     *                              explorer run container mixin
     *
     ******************************************************************************************/

    public class ExplorerRunContainer {
        private Map<String, IExplorerRunInstance> explorerRunMap = new ConcurrentHashMap<>();

        public IExplorerRunInstance create() throws Exception {

            IExplorerRunInstance explorerRunInstance = new RemoteExplorerRunInstance(
                    pipelineRunInstanceUuid,
                    currentEnv);

            explorerRunInstance.create();

            this.explorerRunMap.put(explorerRunInstance.getExplorerRunInstanceUuid(), explorerRunInstance);

            explorerRunStatusUpdateListeners.forEach(listener -> {
                listener.accept(explorerRunInstance.getExplorerRunInstanceUuid(), EnumExplorerRunStatus.STATUS_STARTED);
            });

            return explorerRunInstance;
        }

        public void destroy(String explorerRunInstanceUuid) throws Exception {
            IExplorerRunInstance explorerRun = this.explorerRunMap.get(explorerRunInstanceUuid);
            if(null != explorerRun){
                explorerRun.destroy();
                this.explorerRunMap.remove(explorerRunInstanceUuid);

                explorerRunStatusUpdateListeners.forEach(listener -> {
                    listener.accept(explorerRun.getExplorerRunInstanceUuid(), EnumExplorerRunStatus.STATUS_STOPPED);
                });
            }
        }

        public void clear(){
            for(String explorerRunInstanceUuid : this.explorerRunMap.keySet()){
                try {
                    destroy(explorerRunInstanceUuid);
                } catch (Exception e) {
                    logger.error("destroy explorer run error", e);
                }
            }
        }

        public IExplorerRunInstance get(String explorerRunInstanceUuid){
            return this.explorerRunMap.get(explorerRunInstanceUuid);
        }

    }

    public ExplorerRunContainer getCurrentEnvExplorerRunContainer() {
        return currentEnvExplorerRunContainer;
    }
}