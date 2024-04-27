package com.airxiechao.y20.common.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("common.yml")
public class CommonConfig {

    private ServerConfig server;
    private ConsulConfig consul;
    private RedisConfig redis;
    private RabbitmqConfig rabbitmq;
    private MinIoConfig minio;
    private MySqlConfig mysql;
    private MongoDbConfig mongodb;
    private InfluxDbConfig influxdb;
    private AuthCommonConfig auth;
    private VariableCommonConfig variable;
    private MigrateConfig migrate;

    private CommonConfig(){}

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public ConsulConfig getConsul() {
        return consul;
    }

    public void setConsul(ConsulConfig consul) {
        this.consul = consul;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    public RabbitmqConfig getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(RabbitmqConfig rabbitmq) {
        this.rabbitmq = rabbitmq;
    }

    public MinIoConfig getMinio() {
        return minio;
    }

    public void setMinio(MinIoConfig minio) {
        this.minio = minio;
    }

    public MySqlConfig getMysql() {
        return mysql;
    }

    public void setMysql(MySqlConfig mysql) {
        this.mysql = mysql;
    }

    public MongoDbConfig getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDbConfig mongodb) {
        this.mongodb = mongodb;
    }

    public InfluxDbConfig getInfluxdb() {
        return influxdb;
    }

    public void setInfluxdb(InfluxDbConfig influxdb) {
        this.influxdb = influxdb;
    }

    public AuthCommonConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthCommonConfig auth) {
        this.auth = auth;
    }

    public VariableCommonConfig getVariable() {
        return variable;
    }

    public void setVariable(VariableCommonConfig variable) {
        this.variable = variable;
    }

    public MigrateConfig getMigrate() {
        return migrate;
    }

    public void setMigrate(MigrateConfig migrate) {
        this.migrate = migrate;
    }
}
