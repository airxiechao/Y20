package com.airxiechao.y20.artifact.biz.api;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.axcboot.storage.fs.common.FsFile;

import java.io.InputStream;
import java.util.List;

@IBiz
public interface IArtifactBiz {

    // any file

    List<FsFile> listFile(String path);
    void writeFile(String path, FileData fileData) throws Exception;
    InputStream readFile(String path) throws Exception;
    boolean removeFile(String path);

    // project file

    List<FsFile> listProjectFile(Long userId, Long projectId, String dir);
    String writeProjectFile(Long userId, Long projectId, String dir, FileData fileData) throws Exception;
    InputStream readProjectFile(Long userId, Long projectId, String dir, String name) throws Exception;
    boolean removeProjectFile(Long userId, Long projectId, String dir, String name);

    // pipeline file

    List<FsFile> listPipelineFile(Long userId, Long projectId, Long pipelineId, String dir);
    String writePipelineFile(Long userId, Long projectId, Long pipelineId, String dir, FileData fileData) throws Exception;
    InputStream readPipelineFile(Long userId, Long projectId, Long pipelineId, String dir, String name) throws Exception;
    boolean removePipelineFile(Long userId, Long projectId, Long pipelineId, String dir, String name);
    boolean copyPipelineFile(Long userId, Long fromProjectId, Long fromPipelineId, Long toProjectId, Long toPipelineId);
    boolean existsPipelineFile(Long userId, Long projectId, Long pipelineId);

    // pipeline step file

    String writePipelineStepFile(Long userId, Long projectId, Long pipelineId, FileData fileData) throws Exception;
    InputStream readPipelineStepFile(Long userId, Long projectId, Long pipelineId, String name) throws Exception;

    // pipeline temp file

    String writePipelineTempFile(Long userId, Long projectId, Long pipelineId, FileData fileData) throws Exception;
    InputStream readPipelineTempFile(Long userId, Long projectId, Long pipelineId, String name) throws Exception;

    // pipeline run file

    List<FsFile> listPipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir);
    String writePipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir, FileData fileData) throws Exception;
    InputStream readPipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir, String name) throws Exception;

    // template file
    List<FsFile> listTemplateFile(Long templateId, String dir);
    boolean existsTemplateFile(Long templateId);
    boolean copyTemplateFileFrom(Long userId, Long projectId, Long fromPipelineId, Long templateId);
    boolean copyTemplateFileTo(Long userId, Long projectId, Long fromPipelineId, Long templateId);
    boolean removeTemplateFile(Long templateId);

}
