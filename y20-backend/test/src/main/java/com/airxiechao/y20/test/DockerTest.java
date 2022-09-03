package com.airxiechao.y20.test;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.nio.ByteBuffer;

public class DockerTest {

    public static void main(String[] args) throws Exception {
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            // start container
            String id = startContainer(client, "maven:3.6.3-jdk-11", "d:/test");

            // execute command
//            executeCommand(client, id, "ls /data");
//            executeCommand(client, id, "mvn install -DskipTests");
            executeCommand(client, id, "for i in 1 2 3 4 5; do echo \"Hit $i\"; sleep 1; done");

            // Kill container
            stopContainer(client, id);
        }
    }

    public static String startContainer(DockerClient client, String image, String hostWorkingDir) throws Exception {
        // pull image
        int[] count = {0};
        client.pull(image, (message)->{
            String id = message.id();
            if(null != id){
                System.out.print(id+ ": ");
            }
            System.out.println(message.status());
            String progress = message.progress();
            if(null != progress){
                System.out.println(progress);
            }
            count[0] += 1;
//            if(count[0] > 5){
//                System.out.println("-------------");
//                throw new DockerException("-------");
//            }
        });

        // start container
        HostConfig hostConfig = HostConfig.builder()
                .appendBinds(HostConfig.Bind.from("D:/test").to("/data").build())
                .appendBinds(HostConfig.Bind.from("C:/Users/Administrator/.m2").to("/root/.m2").build())
                .build();
        ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(image)
                .workingDir("/data")
                .cmd("bash")
                .tty(true)
                .build();

        ContainerCreation creation = client.createContainer(containerConfig);
        String id = creation.id();
        client.startContainer(id);

        return id;
    }

    public static void stopContainer(DockerClient client, String containerId) throws Exception {
        client.killContainer(containerId);
        client.removeContainer(containerId);
    }

    public static Long executeCommand(DockerClient client, String containerId, String command) throws Exception {
        String[] commands = {"bash", "-c", command};
        ExecCreation execCreation = client.execCreate(
                containerId, commands,
                DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        LogStream output = client.execStart(execCreation.id());
        while(output.hasNext()){
            ByteBuffer buffer = output.next().content();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            System.out.print(new String(bytes));
        }

        ExecState state = client.execInspect(execCreation.id());
        Long exitCode = state.exitCode();
        return exitCode;
    }
}
