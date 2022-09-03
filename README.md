# 鲲擎流水线（Y20）

[y20.work](https://y20.work) 是一款针对分布式系统的流水线工具。通过编排流水线实现流程的标准化、自动化！:heart:

## 系统组成
整个系统由前端模块、后端微服务、微服务中间件和各种存储设施组成。
 
- 微服务中间件：使用Consul作为注册注册中心，Openresty作为网关。Openresty通过Lua脚本，根据请求Url动态查询从Consul查询相关服务的地址，然后转发。

- 后端服务：各个独立运行的Rest、Rpc服务，注册到Consul。每个服务都有自己的配置文件和独立的数据库。

- 前端模块：前端分模块，每个模块有独立的js、css文件，通过Openresty提供前端服务。首先会加载nav模块（含index.html），然后nav模块再根据Url路径动态加载其他前端模块。

- 存储设施：MySQL、Redis、RabbitMQ、MinIO、MongoDB。

## 后端代码
后端按微服务划分模块：
- activity 项目动态
- agent 节点程序
- artifact 制品库
- atuh 用户认证
- common 公共基础
- cron 定时任务
- email 邮件发送
- ip IP归属地
- log 流水线日志
- manmachinetest 人机测试
- monitor 监控
- pay 支付
- pipeline 流水线
- project 项目
- quota 配额
- sms 发短信
- sql 数据库维护
- template 模板
- test 测试
- tool 工具
- util 辅助类

每个微服务模块又划分成3个子模块：
- api：声明各种普通对象、常量、配置对象、数据库对象、REST接口、RPC接口、业务操作接口、数据库操作接口
- biz：REST接口实现、RPC接口实现、业务操作接口实现、数据库操作接口实现
- boot：微服务的启动模块，启动REST服务、RPC服务

微服务基于 [axcboot](https://github.com/airxiechao/axcboot) 开发。

## 后端编译
1. 编译 [axcboot](https://github.com/airxiechao/axcboot) 
```
git clone https://github.com/airxiechao/axcboot.git

cd axcboot
mvn install -DskipTests
```
2. 编译 Y20 后端项目
```
https://github.com/airxiechao/Y20.git

cd Y20/y20-backend
mvn install -DskipTests
```
3. 各个模块启动项目的输出是相应target文件夹里的lib文件夹和jar文件

## 数据库的初始化和变更
1. 数据库结构通过代码自动提取并初始化
```
cd Y20/y20-backend/sql
mvn clean install -P sql-initialize

cd target
java -jar sql-initialize.jar
```
2. 数据库结构变动也通过代码自动比对并变更
```
cd Y20/y20-backend/sql
mvn clean install -P sql-migrate

cd target
java -jar sql-migrate.jar
```

## 后端配置
后端配置文件在 `Y20/y20-config`，包含：
- 全局通用配置 common.yml
- 每个服务的配置 服务名.yml
- 每个服务的数据库配置 mybatis-y20-服务名.xml
- 日志配置 log4j2.xml

## 前端代码
前端划分模块：
- common 公共依赖
- docs 文档
- home 首页
- login 登录
- nav 入口
- project-file 项目文件
- project-monitor 项目监视
- project-pipeline 项目流水线
- project-variable 项目变量
- signup 注册
- user-account 用户帐户
- user-billing 用户配额
- user-team 用户团队
- user-token 用户令牌
- workspace-agent 节点
- workspace-project 项目
- workspace-template 应用

入口项目是nav，提供入口html，并在运行时根据路径加载其他模块的js和css。

其他模块都是一个项目，提供相应模块的route和store。在编译的时候，会自动将该模块的js和css的文件名写入到 `modules_bundle.json` 文件，以供nav模块查询。

前端基于 vue/vue-router/vuex 开发。

## 前端编译
1. 编译前端模块
```
cd Y20/y20-frontend
yarn install
yarn workspaces run build
```
2. 编译文档
```
cd Y20/y20-frontend/docs

yarn install
yarn build
```

## 网关配置
Openresty网关提供前端服务并通过查询Consul转发后端请求到相应服务，配置在 `Y20/y20-gateway/conf`

## 系统部署
- 安装 Openresty
- 安装 Consul
- 安装 Redis
- 安装 RabbitMQ
- 安装 MySQL
- 安装 MongoDB
- 安装 MinIO
- 部署前端程序
- 部署后端配置
- 数据库初始化
- 部署后端服务
- 打包节点agent

