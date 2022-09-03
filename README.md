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

## 系统部署

