package com.airxiechao.y20.common.git;

import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class GitUtil {
    private static final Logger logger = LoggerFactory.getLogger(GitUtil.class);

    public static String getGitVersion(){
        String version = "UNKNOWN";

        JSONObject jsonObject = getGitJsonObject();
        String build = jsonObject.getString("git.build.version");
        String abbrev = jsonObject.getString("git.commit.id.abbrev");
        if(!StringUtil.isBlank(build) && !StringUtil.isBlank(abbrev)){
            version = String.format("%s.%s", build, abbrev);
        }

        return version;
    }

    public static Date getGitBuildTime(){
        JSONObject jsonObject = getGitJsonObject();
        Date buildTime = jsonObject.getDate("git.build.time");
        return buildTime;
    }

    private static JSONObject getGitJsonObject(){
        IFs resourceFs = new JavaResourceFs();
        String gitPropertiesName = "git.properties";
        if(resourceFs.exist(gitPropertiesName)){
            try(InputStream inputStream = resourceFs.getInputStream(gitPropertiesName)) {
                String strProperties = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject jsonObject = JSON.parseObject(strProperties);
                return jsonObject;
            } catch (Exception e) {
                logger.error("read git properties error", e);
            }
        }

        return new JSONObject();
    }
}
