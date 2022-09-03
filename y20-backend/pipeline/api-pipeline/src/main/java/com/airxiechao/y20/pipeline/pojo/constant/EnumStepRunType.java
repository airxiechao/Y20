package com.airxiechao.y20.pipeline.pojo.constant;

public class EnumStepRunType {

    public static final String PREPARE_DOCKER_ENV_TYPE = "prepare-docker-env";
    public static final String PREPARE_DOCKER_ENV_NAME = "准备Docker环境";

    public static final String DESTROY_DOCKER_ENV_TYPE = "destroy-docker-env";
    public static final String DESTROY_DOCKER_ENV_NAME = "销毁Docker环境";

    public static final String START_DOCKER_ENV_TYPE = "start-docker-env";
    public static final String START_DOCKER_ENV_NAME = "启动Docker环境";

    public static final String STOP_DOCKER_ENV_TYPE = "stop-docker-env";
    public static final String STOP_DOCKER_ENV_NAME = "停止Docker环境";

    public static final String PREPARE_NATIVE_ENV_TYPE = "prepare-native-env";
    public static final String PREPARE_NATIVE_ENV_NAME = "准备本地环境";

    public static final String DESTROY_NATIVE_ENV_TYPE = "destroy-native-env";
    public static final String DESTROY_NATIVE_ENV_NAME = "销毁本地环境";

    public static final String START_NATIVE_ENV_TYPE = "start-native-env";
    public static final String START_NATIVE_ENV_NAME = "启动本地环境";

    public static final String STOP_NATIVE_ENV_TYPE = "stop-native-env";
    public static final String STOP_NATIVE_ENV_NAME = "停止本地环境";

    public static final String SCRIPT_IN_DOCKER_ENV_TYPE = "script-in-docker-env";
    public static final String SCRIPT_IN_DOCKER_ENV_NAME = "Docker中执行脚本";

    public static final String SCRIPT_IN_DOCKER_PROCESS_TYPE = "script-in-docker-process";
    public static final String SCRIPT_IN_DOCKER_PROCESS_NAME = "Docker进程中执行脚本";

    public static final String LINUX_SCRIPT_NOT_IN_DOCKER_ENV_TYPE = "linux-script-not-in-docker-env";
    public static final String LINUX_SCRIPT_NOT_IN_DOCKER_ENV_NAME = "本地执行Linux脚本";

    public static final String PROGRAM_IN_DOCKER_ENV_TYPE = "program-in-docker-env";
    public static final String PROGRAM_IN_DOCKER_ENV_NAME = "Docker中执行程序";

    public static final String WINDOWS_PROGRAM_NOT_IN_DOCKER_ENV_TYPE = "windows-program-not-in-docker-env";
    public static final String WINDOWS_PROGRAM_NOT_IN_DOCKER_ENV_NAME = "本地执行Windows程序";

    public static final String LINUX_PROGRAM_NOT_IN_DOCKER_ENV_TYPE = "linux-program-not-in-docker-env";
    public static final String LINUX_PROGRAM_NOT_IN_DOCKER_ENV_NAME = "本地执行Linux程序";

    public static final String PULL_ARTIFACT_FILE_TYPE = "pull-artifact-file";
    public static final String PULL_ARTIFACT_FILE_NAME = "拉取制品库文件";

    public static final String PUSH_ARTIFACT_FILE_TYPE = "push-artifact-file";
    public static final String PUSH_ARTIFACT_FILE_NAME = "推送制品库文件";

    public static final String TEST_TYPE = "test";
    public static final String TEST_NAME = "测试";

    public static final String WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_TYPE = "windows-script-not-in-docker-env";
    public static final String WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_NAME = "本地执行Windows脚本";

    public static final String REMOTE_DOWNLOAD_FILE_TYPE = "remote-download-file";
    public static final String REMOTE_DOWNLOAD_FILE_NAME = "下载文件";

    public static final String REMOTE_UPLOAD_FILE_TYPE = "remote-upload-file";
    public static final String REMOTE_UPLOAD_FILE_NAME = "上传文件";

    public static final String REMOTE_PLUGIN_TENCENT_COSCMD_TYPE = "remote-plugin-tencent-coscmd";
    public static final String REMOTE_PLUGIN_TENCENT_COSCMD_NAME = "COSCMD 工具";

    public static final String REMOTE_PREPARE_ENV_TYPE = "remote-prepare-env";
    public static final String REMOTE_PREPARE_ENV_NAME = "准备环境";

    public static final String REMOTE_START_ENV_TYPE = "remote-start-env";
    public static final String REMOTE_START_ENV_NAME = "启动环境";

    public static final String REMOTE_STOP_ENV_TYPE = "remote-stop-env";
    public static final String REMOTE_STOP_ENV_NAME = "停止环境";

    public static final String REMOTE_DESTROY_ENV_TYPE = "remote-destroy-env";
    public static final String REMOTE_DESTROY_ENV_NAME = "销毁环境";

    public static final String REMOTE_SCRIPT_TYPE = "remote-script";
    public static final String REMOTE_SCRIPT_NAME = "执行脚本";

    public static final String REMOTE_PROGRAM_TYPE = "remote-program";
    public static final String REMOTE_PROGRAM_NAME = "执行程序";

    public static final String REMOTE_CALL_PIPELINE_TYPE = "remote-call-pipeline";
    public static final String REMOTE_CALL_PIPELINE_NAME = "调用流水线";

}
