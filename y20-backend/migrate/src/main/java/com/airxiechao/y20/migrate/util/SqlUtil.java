package com.airxiechao.y20.migrate.util;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.db.api.IUserDb;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.airxiechao.y20.common.core.db.DbManagerUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.output.DiffOutputControl;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.FileSystemResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {

    private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    /**
     * get all db names
     * @return
     */
    public static List<String> getAllDbNames(){
        String mybatisRegex = "mybatis-y20-(.+).xml";
        Pattern mybatisPattern = Pattern.compile(mybatisRegex);

        // db names
        Map<String, String> dbNameMap = new HashMap<>();
        dbNameMap.put("default", "y20_base");
        Set<Class<?>> dbApiSet = ClsUtil.getTypesAnnotatedWith(Meta.getProjectPackageName(), IDb.class);
        for(Class dbApi : dbApiSet){
            IDb dbAnnotation = AnnotationUtil.getClassAnnotation(dbApi, IDb.class);
            String configFilePath = dbAnnotation.value();
            if(StringUtil.isBlank(configFilePath)){
                continue;
            }

            Matcher matcher = mybatisPattern.matcher(configFilePath);
            if(!matcher.find()){
                continue;
            }

            String dbName = "y20_" + matcher.group(1);
            String pkg = dbApi.getPackageName();
            if(!pkg.endsWith(".api")){
                continue;
            }

            pkg = pkg.substring(0, pkg.indexOf(".api"));

            dbNameMap.put(pkg, dbName);
        }

        return new ArrayList<>(dbNameMap.values());
    }

    /**
     * create databases and tables
     */
    public static void createDb(DbManager dbManager, boolean dummyDatabase, boolean ddlTable){
        String mybatisRegex = "mybatis-y20-(.+).xml";
        Pattern mybatisPattern = Pattern.compile(mybatisRegex);

        // db names
        Map<String, String> dbNameMap = new HashMap<>();
        dbNameMap.put("default", "y20_base" + (dummyDatabase ? "_tmp" : ""));
        Set<Class<?>> dbApiSet = ClsUtil.getTypesAnnotatedWith(Meta.getProjectPackageName(), IDb.class);
        for(Class dbApi : dbApiSet){
            IDb dbAnnotation = AnnotationUtil.getClassAnnotation(dbApi, IDb.class);
            String configFilePath = dbAnnotation.value();
            if(StringUtil.isBlank(configFilePath)){
                continue;
            }

            Matcher matcher = mybatisPattern.matcher(configFilePath);
            if(!matcher.find()){
                continue;
            }

            String dbName = "y20_" + matcher.group(1);
            if(dummyDatabase){
                dbName += "_tmp";
            }
            String pkg = dbApi.getPackageName();
            if(!pkg.endsWith(".api")){
                continue;
            }

            pkg = pkg.substring(0, pkg.indexOf(".api"));

            dbNameMap.put(pkg, dbName);
        }

        // create database
        for(String dbName : dbNameMap.values()){
            logger.info("create database if not exists [{}]", dbName);
            dbManager.updateBySql("CREATE DATABASE IF NOT EXISTS `" +
                    dbName + "` DEFAULT CHARACTER SET utf8mb4;");
        }

        // ddl table
        if(ddlTable){
            Set<Class<?>> tables = ClsUtil.getTypesAnnotatedWith(Meta.getProjectPackageName(), Table.class);
            for(Class<?> table : tables){

                if(!table.getSimpleName().endsWith("Record")){
                    continue;
                }

                String pkg = table.getPackageName();
                pkg = pkg.substring(0, pkg.indexOf(".record"));
                String dbName = dbNameMap.get(pkg);
                if(StringUtil.isBlank(dbName)){
                    dbName = dbNameMap.get("default");
                }
                dbManager.updateBySql("use " + dbName + ";");

                logger.info("execute table ddl if not exists [{}]", dbName);

                String sqlDdl = DbUtil.ddl(table, false);

                String[] statements = sqlDdl.split(";\r?\n");
                for(String statement : statements){
                    dbManager.updateBySql(statement);
                }
            }
        }
    }

    /**
     * diff and update single database
     * @param dbManager
     * @param username
     * @param password
     * @param dbName
     * @throws Exception
     */
    public static void diffAndUpdateDb(
            DbManager dbManager,
            String username,
            String password,
            String dbName
    ) throws Exception {
        String changeLogFileName = "changelog.xml";
        File changeLogFile = new File(changeLogFileName);

        if(changeLogFile.exists()){
            changeLogFile.delete();
        }

        String dbNameTmp = dbName + "_tmp";

        try(
                java.sql.Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/" + dbName + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8",
                        username,
                        password);

                java.sql.Connection connectionTmp = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/" + dbNameTmp + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8",
                        username,
                        password);
        ) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Database databaseTmp = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connectionTmp));

            logger.info("diff database [{}]", dbName);
            CommandLineUtils.doDiffToChangeLog(changeLogFileName, databaseTmp, database, new DiffOutputControl(), null, null);

            String changeLogContent = Files.readString(changeLogFile.toPath());
            changeLogContent = changeLogContent.replace(dbNameTmp, dbName);
            Pattern createFtIndexPat = Pattern.compile("(<createIndex.+indexName=\"ft-.+</createIndex>)", Pattern.DOTALL);
            Matcher createFtIndexMat = createFtIndexPat.matcher(changeLogContent);
            String ftModifiedChangeLogContent = changeLogContent;
            while(createFtIndexMat.find()){
                String createFtIndex = createFtIndexMat.group(1);
                ftModifiedChangeLogContent = ftModifiedChangeLogContent.replace(createFtIndex, createFtIndex +
                        "<modifySql><replace replace=\"INDEX\" with=\"FULLTEXT INDEX\"/><append value=\"WITH PARSER ngram\"/></modifySql>");
            }

            Files.writeString(changeLogFile.toPath(), ftModifiedChangeLogContent);

            logger.info("update database [{}]", dbName);
            Liquibase liquibase = new Liquibase(changeLogFileName, new FileSystemResourceAccessor(new File(".")), database);
            liquibase.update("Update");


        } finally {
            logger.info("delete dummy database [{}]", dbNameTmp);

            // drop temp database
            dbManager.updateBySql("drop database " + dbNameTmp + ";");

            if(changeLogFile.exists()){
                changeLogFile.delete();
            }
        }
    }

    /**
     * 创建首个用户
     */
    public static void createFirstUser(Long userId, String username, String password, String mobile){
        // user
        logger.info("insert user [{}:{}]", userId, username);
        String userDbConfigFile = IUserDb.class.getAnnotation(IDb.class).value();
        DbManager userDbManager = DbManagerUtil.createDbManager(new JavaResourceFs(), userDbConfigFile);
        userDbManager.updateBySql("delete from " + UserRecord.class.getAnnotation(Table.class).value());

        UserRecord userRecord = new UserRecord();
        userRecord.setId(userId);
        userRecord.setUsername(username);
        userRecord.setPasswordHashed(AuthUtil.hashPassword(password));
        userRecord.setMobile(mobile);
        userDbManager.insert(userRecord);
    }
}
