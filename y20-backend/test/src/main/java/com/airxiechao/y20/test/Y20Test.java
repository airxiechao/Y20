package com.airxiechao.y20.test;

import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.common.core.biz.Biz;

public class Y20Test {
    public static void main(String[] args) throws Exception {

        IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);
        String token = accessTokenBiz.createUserAccessToken(4L, null);
        System.out.println(token);

        /*
        IAppRun appRun = Biz.get(IAppBiz.class).createRun("test", new MapBuilder().build(), log -> {
            System.out.print(log);
        }).setEnv("p1", new Env("aaa", null, "d:/test", null));

        Thread t = new Thread(()->{
            try {
                while(true){
                    Thread.sleep(10000);
                    appRun.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
        */


        /*
        //while (true) {
            Thread.sleep(1000 * 5);

            var pipeline = new Pipeline()
                    .addStep(
                            new Step()
                                    .setType(RemotePrepareEnvStepRun.TYPE)
                                    .setParam(RemotePrepareEnvStepRun.PARAM_AGENT_ID, "a")
                                    .setParam(RemotePrepareEnvStepRun.PARAM_IMAGE, "maven:3.6.3-jdk-11")
                                    .setParam(RemotePrepareEnvStepRun.PARAM_HOST_WORKING_DIR, "d:/test")
                                    .setParam(RemotePrepareEnvStepRun.PARAM_WORKING_DIR, "/data")
                    )
                    .addStep(
                            new Step()
                                    .setType(TestStepRun.TYPE)
                                    .setParam(TestStepRun.PARAM_I, 5)
                                    .setParam(TestStepRun.PARAM_SCRIPT, "for i in 1 2 3 4 5; do echo \"Hit $i\"; sleep 1; done")
                                    //.setParam(TestStepRun.PARAM_SCRIPT, "a=1\nb=1\nif ((a==b))\nthen\n    echo '$a==$b'\nfi")
                                    //.setType(WindowsCmdScriptNotInDockerEnvStepRun.TYPE)
                                    //.setParam(WindowsCmdScriptNotInDockerEnvStepRun.PARAM_SCRIPT, "dir")
                                    //.setParam(WindowsCmdScriptNotInDockerEnvStepRun.PARAM_WORKING_DIR, ".")
                    );

            PipelineRunRecord runRecord = Biz.get(IPipelineBiz.class).createPipelineRun(pipeline);
            Biz.get(IPipelineBiz.class).startRunAsync(runRecord.getId()).get();
        //}
        */

        /*
        PipelineRun pipelineRun = PipelineRunFactory.getInstance().buildPipelineRun(
                pipeline,
                new Env(
                        "aaa",
                        "maven:3.6.3-jdk-11",
                        "d:/test",
                        "/data"),
                (log)->{
                    System.out.print(log);
                });

        AtomicBoolean stop = new AtomicBoolean(false);
        Thread t = new Thread(()->{
            try {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    String line = scanner.nextLine();
                    stop.set(true);
                    pipelineRun.stop();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();

        while (!stop.get()){

            try {
                pipelineRun.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread.sleep(1000*10);
        }

        try {
            PipelineRunFactory.getInstance().destroyPipelineRun(pipelineRun);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        agentRpcServer.syncStop();

        */
    }
}
