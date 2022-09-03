package com.airxiechao.y20.artifact.biz.process;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.artifact.biz.api.IArtifactBiz;
import com.airxiechao.y20.artifact.fs.MinIoFsFactory;
import com.airxiechao.y20.common.artifact.ArtifactUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ArtifactBizProcess implements IArtifactBiz {

    private static final IFs fs = MinIoFsFactory.getInstance().getFs();

    @Override
    public List<FsFile> listFile(String path) {
        return fs.list(path);
    }

    @Override
    public void writeFile(String path, FileData fileData) throws Exception {
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
    }

    @Override
    public InputStream readFile(String path) throws Exception {
        return fs.getInputStream(path);
    }

    @Override
    public boolean removeFile(String path) {
        return fs.remove(path);
    }

    @Override
    public List<FsFile> listProjectFile(Long userId, Long projectId, String dir) {
        return fs.list(ArtifactUtil.getProjectFilePath(userId, projectId, dir, null));
    }

    @Override
    public String writeProjectFile(Long userId, Long projectId, String dir, FileData fileData) throws Exception {
        String path = ArtifactUtil.getProjectFilePath(userId, projectId, dir, fileData.getName());
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
        return path;
    }

    @Override
    public InputStream readProjectFile(Long userId, Long projectId, String dir, String name) throws Exception {
        return fs.getInputStream(ArtifactUtil.getProjectFilePath(userId, projectId, dir, name));
    }

    @Override
    public boolean removeProjectFile(Long userId, Long projectId, String dir, String name) {
        String path = ArtifactUtil.getProjectFilePath(userId, projectId, dir, name);
        return fs.remove(path);
    }

    @Override
    public List<FsFile> listPipelineFile(Long userId, Long projectId, Long pipelineId, String dir) {
        return fs.list(ArtifactUtil.getPipelineFilePath(userId, projectId, pipelineId, dir, null));
    }

    @Override
    public String writePipelineFile(Long userId, Long projectId, Long pipelineId, String dir, FileData fileData) throws Exception {
        String path = ArtifactUtil.getPipelineFilePath(userId, projectId, pipelineId, dir, fileData.getName());
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
        return path;
    }

    @Override
    public InputStream readPipelineFile(Long userId, Long projectId, Long pipelineId, String dir, String name) throws Exception {
        return fs.getInputStream(ArtifactUtil.getPipelineFilePath(userId, projectId, pipelineId, dir, name));
    }

    @Override
    public boolean removePipelineFile(Long userId, Long projectId, Long pipelineId, String dir, String name) {
        String path = ArtifactUtil.getPipelineFilePath(userId, projectId, pipelineId, dir, name);
        return fs.remove(path);
    }

    @Override
    public boolean copyPipelineFile(Long userId, Long fromProjectId, Long fromPipelineId, Long toProjectId, Long toPipelineId) {
        String fromPath = ArtifactUtil.getPipelineFilePath(userId, fromProjectId, fromPipelineId, null, null);
        String toPath = ArtifactUtil.getPipelineFilePath(userId, toProjectId, toPipelineId, null, null);
        return fs.copy(fromPath, toPath);
    }

    @Override
    public boolean existsPipelineFile(Long userId, Long projectId, Long pipelineId) {
        String path = ArtifactUtil.getPipelineFilePath(userId, projectId, pipelineId, null, null);
        return fs.exist(path);
    }

    @Override
    public String writePipelineStepFile(Long userId, Long projectId, Long pipelineId, FileData fileData) throws Exception {
        String path = ArtifactUtil.getPipelineStepFilePath(userId, projectId, pipelineId, null, fileData.getName());
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
        return path;
    }

    @Override
    public InputStream readPipelineStepFile(Long userId, Long projectId, Long pipelineId, String name) throws Exception {
        String[] nameTokens = name.split("/");
        int tokenLength = nameTokens.length;
        if(tokenLength < 2){
            throw new Exception("pipeline step file name error");
        }

        return fs.getInputStream(ArtifactUtil.getPipelineStepFilePath(userId, projectId, pipelineId,
                nameTokens[tokenLength - 2], nameTokens[tokenLength - 1]));
    }

    @Override
    public String writePipelineTempFile(Long userId, Long projectId, Long pipelineId, FileData fileData) throws Exception {
        String path = ArtifactUtil.getPipelineTempFilePath(userId, projectId, pipelineId, null, fileData.getName());
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
        return path;
    }

    @Override
    public InputStream readPipelineTempFile(Long userId, Long projectId, Long pipelineId, String name) throws Exception {
        String[] nameTokens = name.split("/");
        int tokenLength = nameTokens.length;
        if(tokenLength < 2){
            throw new Exception("pipeline temp file name error");
        }

        return fs.getInputStream(ArtifactUtil.getPipelineTempFilePath(userId, projectId, pipelineId,
                nameTokens[tokenLength - 2], nameTokens[tokenLength - 1]));
    }

    @Override
    public List<FsFile> listPipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir) {
        return fs.list(ArtifactUtil.getPipelineRunFilePath(userId, projectId, pipelineId, pipelineRunId, dir, null));
    }

    @Override
    public String writePipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir, FileData fileData) throws Exception {
        String path = ArtifactUtil.getPipelineRunFilePath(userId, projectId, pipelineId, pipelineRunId, dir, fileData.getName());
        try(OutputStream outputStream = fs.getOutputStream(path);
            InputStream inputStream = fileData.getFileItem().getInputStream()){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
        return path;
    }

    @Override
    public InputStream readPipelineRunFile(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String dir, String name) throws Exception {
        return fs.getInputStream(ArtifactUtil.getPipelineRunFilePath(userId, projectId, pipelineId, pipelineRunId, dir, name));
    }

    @Override
    public List<FsFile> listTemplateFile(Long templateId, String dir) {
        return fs.list(ArtifactUtil.getTemplateFilePath(templateId, dir, null));
    }

    @Override
    public boolean existsTemplateFile(Long templateId) {
        String path = ArtifactUtil.getTemplateFilePath(templateId, null, null);
        return fs.exist(path);
    }

    @Override
    public boolean copyTemplateFileFrom(Long userId, Long projectId, Long fromPipelineId, Long templateId) {
        String fromPath = ArtifactUtil.getPipelineFilePath(userId, projectId, fromPipelineId, null, null);
        String toPath = ArtifactUtil.getTemplateFilePath(templateId, null, null);
        return fs.copy(fromPath, toPath);
    }

    @Override
    public boolean copyTemplateFileTo(Long userId, Long projectId, Long fromPipelineId, Long templateId) {
        String fromPath = ArtifactUtil.getTemplateFilePath(templateId, null, null);
        String toPath = ArtifactUtil.getPipelineFilePath(userId, projectId, fromPipelineId, null, null);
        return fs.copy(fromPath, toPath);
    }

    @Override
    public boolean removeTemplateFile(Long templateId) {
        String path = ArtifactUtil.getTemplateFilePath(templateId, null, null);
        return fs.remove(path);
    }
}
