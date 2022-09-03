package com.airxiechao.y20.artifact.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.artifact.biz.api.IArtifactBiz;
import com.airxiechao.y20.artifact.rest.api.IUserArtifactRest;
import com.airxiechao.y20.artifact.rest.param.*;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserArtifactRestHandler implements IUserArtifactRest {

    private static final Logger logger = LoggerFactory.getLogger(UserArtifactRestHandler.class);
    private IArtifactBiz artifactBiz = Biz.get(IArtifactBiz.class);

    @Override
    public Response listUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListUserFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            return new Response().error("userid check fail");
        }

        List<FsFile> list = artifactBiz.listFile(path);

        return new Response().data(list);
    }

    @Override
    public Response uploadUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadUserFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            return new Response().error("userid check fail");
        }

        try{
            artifactBiz.writeFile(path, param.getFile());
            return new Response();
        }catch (Exception e){
            logger.error("write user file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response removeUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        RemoveUserFileParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, RemoveUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            return new Response().error("userid check fail");
        }

        boolean removed = artifactBiz.removeFile(path);
        if(!removed){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public void downloadUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadUserFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            logger.error("userid check fail");
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, Paths.get(path).getFileName().toString());

        try(InputStream inputStream = artifactBiz.readFile(param.getPath())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download user file error", e);
            return;
        }
    }

    @Override
    public Response listProjectFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListProjectFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListProjectFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<FsFile> list = artifactBiz.listProjectFile(param.getUserId(), param.getProjectId(), param.getDir());

        return new Response().data(list);
    }

    @Override
    public Response uploadProjectFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadProjectFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadProjectFileParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        try{
            String path = artifactBiz.writeProjectFile(
                    param.getUserId(), param.getProjectId(), param.getDir(), param.getFile());
            return new Response().data(path);
        }catch (Exception e){
            logger.error("upload project file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response removeProjectFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        RemoveProjectFileParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, RemoveProjectFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean removed = artifactBiz.removeProjectFile(param.getUserId(), param.getProjectId(), param.getDir(), param.getName());
        if(!removed){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public void downloadProjectFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadProjectFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadProjectFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, param.getName());

        try(InputStream inputStream = artifactBiz.readProjectFile(
                param.getUserId(), param.getProjectId(), param.getDir(), param.getName())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download project file error", e);
            return;
        }
    }

    @Override
    public Response listPipelineFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<FsFile> list = artifactBiz.listPipelineFile(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getDir());

        return new Response().data(list);
    }

    @Override
    public Response uploadPipelineFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadPipelineFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadPipelineFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        try{
            String path = artifactBiz.writePipelineFile(param.getUserId(), param.getProjectId(),
                    param.getPipelineId(), param.getDir(), param.getFile());
            return new Response().data(path);
        }catch (Exception e){
            logger.error("upload pipeline file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response removePipelineFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        RemovePipelineFileParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, RemovePipelineFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean removed = artifactBiz.removePipelineFile(param.getUserId(), param.getProjectId(), param.getPipelineId(),
                param.getDir(), param.getName());
        if(!removed){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public void downloadPipelineFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadPipelineFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadPipelineFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, param.getName());

        try(InputStream inputStream = artifactBiz.readPipelineFile(
                param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getDir(), param.getName())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download pipeline file error", e);
            return;
        }
    }

    @Override
    public Response uploadPipelineStepFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadPipelineStepFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadPipelineStepFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        try{
            String path = artifactBiz.writePipelineStepFile(param.getUserId(), param.getProjectId(),
                    param.getPipelineId(), param.getFile());
            return new Response().data(path);
        }catch (Exception e){
            logger.error("upload pipeline step file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public void downloadPipelineStepFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadPipelineStepFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadPipelineStepFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, param.getName());

        try(InputStream inputStream = artifactBiz.readPipelineStepFile(
                param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getName())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download pipeline step file error", e);
            return;
        }
    }

    @Override
    public Response uploadPipelineTempFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadPipelineTempFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadPipelineTempFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        try{
            String path = artifactBiz.writePipelineTempFile(param.getUserId(), param.getProjectId(),
                    param.getPipelineId(), param.getFile());
            return new Response().data(path);
        }catch (Exception e){
            logger.error("upload pipeline temp file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public void downloadPipelineTempFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadPipelineTempFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadPipelineTempFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, param.getName());

        try(InputStream inputStream = artifactBiz.readPipelineTempFile(
                param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getName())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download pipeline temp file error", e);
            return;
        }
    }

    @Override
    public Response listPipelineRunFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineRunFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineRunFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<FsFile> list = artifactBiz.listPipelineRunFile(param.getUserId(), param.getProjectId(),
                param.getPipelineId(), param.getPipelineRunId(), param.getDir());

        return new Response().data(list);
    }

    @Override
    public Response uploadPipelineRunFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadPipelineRunFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadPipelineRunFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        try{
            String path = artifactBiz.writePipelineRunFile(param.getUserId(), param.getProjectId(),
                    param.getPipelineId(), param.getPipelineRunId(), param.getDir(), param.getFile());
            return new Response().data(path);
        }catch (Exception e){
            logger.error("upload pipeline run file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public void downloadPipelineRunFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadPipelineRunFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadPipelineRunFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, param.getName());

        try(InputStream inputStream = artifactBiz.readPipelineRunFile(
                param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId(),
                param.getDir(), param.getName())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download pipeline runs file error", e);
            return;
        }
    }

    @Override
    public Response listTemplateFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListTemplateFileParam param = null;
        try {
            param = RestUtil.queryData(exchange, ListTemplateFileParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Long templateId = param.getTemplateId();
        String path = param.getPath();
        if(!StringUtil.isBlank(path)){
            // check templateId in path
            if(!checkTemplateId(templateId, path)){
                return new Response().error("templateId check fail");
            }

            List<FsFile> list = artifactBiz.listFile(path);
            return new Response().data(list);
        }else{
            List<FsFile> list = artifactBiz.listTemplateFile(templateId, param.getDir());
            return new Response().data(list);
        }
    }

    @Override
    public void downloadTemplateFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadTemplateFileParam param = null;
        try {
            param = RestUtil.queryData(exchange, DownloadTemplateFileParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        Long templateId = param.getTemplateId();
        String path = param.getPath();

        // check templateId in path
        if (!checkTemplateId(templateId, path)) {
            logger.error("templateId check fail");
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, Paths.get(path).getFileName().toString());

        try(InputStream inputStream = artifactBiz.readFile(param.getPath())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
            exchange.getOutputStream();
        }catch (Exception e){
            logger.error("download template file error", e);
            return;
        }
    }

    private boolean checkUserId(Long userId, String path){
        Pattern pattern = Pattern.compile("^/user/([^/]+)/.*$");
        Matcher matcher = pattern.matcher(path);
        if(!matcher.find()) {
            return false;
        }

        Long userIdInPath = Long.valueOf(matcher.group(1));
        return userIdInPath == userId;
    }

    private boolean checkTemplateId(Long templateId, String path){
        Pattern pattern = Pattern.compile("^/template/([^/]+)/.*$");
        Matcher matcher = pattern.matcher(path);
        if(!matcher.find()) {
            return false;
        }

        Long userIdInPath = Long.valueOf(matcher.group(1));
        return userIdInPath == templateId;
    }
}
