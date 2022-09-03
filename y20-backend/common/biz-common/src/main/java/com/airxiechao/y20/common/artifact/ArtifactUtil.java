package com.airxiechao.y20.common.artifact;

import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.UuidUtil;

public class ArtifactUtil {
    public static String escapeFileName(String name){
        name = name.replace("/", "_");
        return name;
    }

    public static String getProjectFilePath(Long userId, Long projectId, String dir, String name){
        dir = stripSubDir(dir);

        if(StringUtil.isBlank(name)){
            return String.format("/user/%d/project/%d/file/%s", userId, projectId, dir);
        }else {
            name = escapeFileName(name);
            return String.format("/user/%d/project/%d/file/%s%s", userId, projectId, dir, name);
        }
    }

    public static String getPipelineFilePath(Long userId, Long projectId, Long pipelineId, String dir, String name){
        dir = stripSubDir(dir);

        if(StringUtil.isBlank(name)){
            return String.format("/user/%d/project/%d/pipeline/%d/file/%s", userId, projectId, pipelineId, dir);
        }else {
            name = escapeFileName(name);
            return String.format("/user/%d/project/%d/pipeline/%d/file/%s%s", userId, projectId, pipelineId, dir, name);
        }
    }

    public static String getPipelineStepFilePath(Long userId, Long projectId, Long pipelineId, String uuid, String name){
        if(StringUtil.isBlank(uuid) && StringUtil.isBlank(name)){
            return String.format("/user/%d/project/%d/pipeline/%d/step/", userId, projectId, pipelineId);
        }else {
            if(StringUtil.isBlank(uuid)){
                uuid = UuidUtil.random();
            }
            name = escapeFileName(name);
            return String.format("/user/%d/project/%d/pipeline/%d/step/%s/%s", userId, projectId, pipelineId, uuid, name);
        }
    }

    public static String getPipelineTempFilePath(Long userId, Long projectId, Long pipelineId, String uuid, String name){
        if(StringUtil.isBlank(uuid) && StringUtil.isBlank(name)){
            return String.format("/user/%d/project/%d/pipeline/%d/temp/", userId, projectId, pipelineId);
        }else {
            if(StringUtil.isBlank(uuid)){
                uuid = UuidUtil.random();
            }
            name = escapeFileName(name);
            return String.format("/user/%d/project/%d/pipeline/%d/temp/%s/%s", userId, projectId, pipelineId, uuid, name);
        }
    }

    public static String getPipelineRunFilePath(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir, String name){
        dir = stripSubDir(dir);

        if(StringUtil.isBlank(name)){
            return String.format("/user/%d/project/%d/pipeline/%d/run/%d/file/%s", userId, projectId, pipelineId, pipelineRunId, dir);
        }else {
            name = escapeFileName(name);
            return String.format("/user/%d/project/%d/pipeline/%d/run/%d/file/%s%s", userId, projectId, pipelineId, pipelineRunId, dir, name);
        }
    }

    public static String getTemplateFilePath(Long templateId, String dir, String name){
        dir = stripSubDir(dir);

        if(StringUtil.isBlank(name)){
            return String.format("/template/%d/file/%s", templateId, dir);
        }else {
            name = escapeFileName(name);
            return String.format("/templateId/%d/file/%s%s", templateId, dir, name);
        }
    }

    private static String stripSubDir(String dir){
        if(!StringUtil.isBlank(dir)){
            dir = dir.strip();
            dir = dir.replace("\\", "/");

            while(true){
                int i = dir.indexOf("/./");
                if(i < 0){
                    break;
                }
                dir = dir.substring(0, i) + "/" + dir.substring(i+3);
            }

            while(true){
                int i = dir.indexOf("/../");
                if(i < 0){
                    break;
                }
                dir = dir.substring(0, i) + "/" + dir.substring(i+4);
            }

            while(dir.startsWith("./")){
                dir = dir.substring(2);
            }
            while(dir.startsWith("../")){
                dir = dir.substring(3);
            }
            while(dir.startsWith("/")){
                dir = dir.substring(1);
            }
            while (dir.endsWith("/")){
                dir = dir.substring(0, dir.length() - 1);
            }
        }else{
            dir = "";
        }

        if(!StringUtil.isBlank(dir)){
            dir = dir + "/";
        }

        return dir;
    }
}
