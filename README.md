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

微服务的基于 [axcboot](https://github.com/airxiechao/axcboot) 开发。

## 前端代码

## 后端编译

## 前端编译

## 系统部署

