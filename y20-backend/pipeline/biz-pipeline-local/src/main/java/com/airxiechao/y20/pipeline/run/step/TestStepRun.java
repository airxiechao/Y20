package com.airxiechao.y20.pipeline.run.step;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.TestStepRunParam;
import com.airxiechao.y20.util.DockerUtil;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.TEST_TYPE,
        name = EnumStepRunType.TEST_NAME,
        paramClass = TestStepRunParam.class)
public class TestStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(TestStepRun.class);

    private TestStepRunParam param;

    public TestStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), TestStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        StreamUtil.PipedStream logStream = getPipedStream();
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            Long exitCode =  DockerUtil.executeCommand(client, this.env.getDockerContainerId(), param.getScript(), null, logStream);
            return new Response().data(exitCode);
        }
    }

    @Override
    public Response stop() throws Exception {
        if(!StringUtil.isNullOrEmpty(this.env.getDockerContainerId())){
            try(DockerClient client = DefaultDockerClient.fromEnv().build()){
                DockerUtil.stopContainer(client, this.env.getDockerContainerId());
                DockerUtil.removeContainer(client, this.env.getDockerContainerId());
            }catch (Exception e){
                return new Response().error(e.getMessage());
            }
        }

        return new Response();
    }
}
