package com.airxiechao.y20.util;

import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DockerUtil {

    private static final Logger logger = LoggerFactory.getLogger(DockerUtil.class);

    /**
     * 创建容器
     */
    public static String createContainer(
            DockerClient client,
            String name,
            String image,
            String serverAddress,
            String username,
            String password,
            String hostWorkingDir,
            String workingDir,
            Map<String, String> otherBinds,
            Map<String, String> envVariables,
            StreamUtil.PipedStream logStream,
            Supplier<Boolean> flagStopSupplier
    ) throws Exception {

        // if image does not has tag, add 'latest' tag
        if(image.indexOf(":") < 0){
            image = image + ":latest";
        }

        ProgressHandler progressHandler = (message)->{
            try{

                // check stop flag
                if(flagStopSupplier.get()){
                    throw new DockerException("stop by flag");
                }

                StringBuilder sb = new StringBuilder();
                String id = message.id();
                if(null != id){
                    sb.append(id+ ": ");
                }
                sb.append(message.status());
                String progress = message.progress();
                if(null != progress){
                    sb.append(progress);
                }

                String log = sb.toString();
                logStream.write(log+System.lineSeparator());
                logStream.flush();

                logger.debug(log.strip());
            }catch (DockerException e){
                throw e;
            }catch (Exception e){

            }
        };

        // pull image
        if(!StringUtil.isBlank(serverAddress) && !StringUtil.isBlank(username) && !StringUtil.isBlank(password)){
            RegistryAuth registryAuth = RegistryAuth.builder()
                    .serverAddress(serverAddress)
                    .username(username)
                    .password(password)
                    .build();

            client.pull(image, registryAuth, progressHandler);
        }else{
            client.pull(image, progressHandler);
        }

        // create container
        HostConfig.Builder configBuilder = HostConfig.builder()
                //.autoRemove(true)
                .appendBinds(HostConfig.Bind.from(hostWorkingDir).to(workingDir).build());

        // other binds
        if(null != otherBinds){
            otherBinds.entrySet().forEach(entry -> {
                String hostDir = entry.getKey();
                String targetDir = entry.getValue();
                configBuilder.appendBinds(HostConfig.Bind.from(hostDir).to(targetDir).build());
            });
        }

        HostConfig hostConfig = configBuilder.build();
        ContainerConfig.Builder containerBuilder = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(image)
                .workingDir(workingDir)
                //.cmd("bash")
                .tty(true);

        // env
        if(null != envVariables){
            List<String> envList = envVariables.entrySet().stream().map(entry -> {
                return entry.getKey()+"="+entry.getValue();
            }).collect(Collectors.toList());
            containerBuilder.env(envList);
        }

        ContainerConfig containerConfig = containerBuilder.build();

        ContainerCreation creation = client.createContainer(containerConfig, name);

        String id = creation.id();
        return id;
    }

    /**
     * 启动容器
     */
    public static void startContainer(DockerClient client, String containerId) throws Exception {
        client.startContainer(containerId);
    }

    /**
     * 容器是否在运行
     */
    public static boolean isContainerRunning(DockerClient client, String containerId) throws Exception {
        return client.inspectContainer(containerId).state().running();
    }

    /**
     * 获取容器信息
     */
    public static ContainerInfo inspectContainer(DockerClient client, String containerId) throws Exception {
        return client.inspectContainer(containerId);
    }

    /**
     * 停止容器
     */
    public static void stopContainer(DockerClient client, String containerId) throws Exception {
        client.killContainer(containerId);
    }

    /**
     * 销毁容器
     */
    public static void removeContainer(DockerClient client, String containerId) throws Exception {
        client.removeContainer(containerId);
    }

    /**
     * 执行命令
     */
    public static Long executeCommand(
            DockerClient client,
            String containerId,
            String command,
            String executor,
            StreamUtil.PipedStream logStream
    ) throws Exception {
        if(StringUtil.isBlank(executor)){
            executor = "bash";
        }
        String[] commands = {executor, "-c", command};
        ExecCreation execCreation = client.execCreate(
                containerId, commands,
                DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        LogStream output = client.execStart(execCreation.id());
        while(output.hasNext()){
            ByteBuffer buffer = output.next().content();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            String log = new String(bytes);
            logStream.write(log);
            logStream.flush();
        }

        ExecState state = client.execInspect(execCreation.id());
        Long exitCode = state.exitCode();
        return exitCode;

    }

    /**
     * 执行命令
     */
    public static String executeCommand(
            DockerClient client,
            String containerId,
            String command,
            String executor
    ) throws Exception {
        if(StringUtil.isBlank(executor)){
            executor = "bash";
        }
        String[] commands = {executor, "-c", command};
        ExecCreation execCreation = client.execCreate(
                containerId, commands,
                DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        LogStream output = client.execStart(execCreation.id());
        return output.readFully();
    }

}
