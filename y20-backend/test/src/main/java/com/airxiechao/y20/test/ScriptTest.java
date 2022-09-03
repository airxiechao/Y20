package com.airxiechao.y20.test;

import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.CharsetUtil;

public class ScriptTest {
    public static void main(String[] args) throws Exception {
        testScript();
    }

    public static void testDestroy() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe");
        builder.redirectErrorStream(true);
        Process process = builder.start();

        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
        }).start();

        System.out.println(">>>>>>>>>>>>");
        StreamUtil.readStringInputStream(process.getInputStream(), 200, CharsetUtil.fromSystem(), (log)->{
            System.out.println(log);
        });
        process.waitFor();
        System.out.println("-----------------");
    }

    public static void testScript(){
        /*
        var run = new LinuxShellScriptInDockerProcessStepRun(null, "6589f3f8301f");
        run.assemble(new Step()
                .setParam(LinuxShellScriptInDockerProcessStepRun.PARAM_SCRIPT, "while :; do echo 'Hit CTRL+C'; sleep 1; done")
                //.setParam(LinuxShellScriptInDockerProcessStepRun.PARAM_SCRIPT, "ls")
                .setParam(LinuxShellScriptInDockerProcessStepRun.PARAM_WORKING_DIR, "/")
        );

        new Thread(()->{
            try {
                Thread.sleep(3000);
                run.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        try {
            run.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
