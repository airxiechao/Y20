# 鲲擎流水线（Y20）

[y20.work](https://y20.work) 是一款针对分布式系统的流水线工具。通过编排流水线实现流程的标准化、自动化！:heart:

## 系统组成
整个系统由前端模块、后端微服务、微服务中间件和各种存储设施组成。
 
- 微服务中间件：使用Consul作为注册注册中心，Openresty作为网关。Openresty通过Lua脚本，根据请求Url动态查询从Consul查询相关服务的地址，然后转发。

- 后端服务：各个独立运行的Rest、Rpc服务，注册到Consul。每个服务都有自己的配置文件和独立的数据库。

- 前端模块：前端分模块，每个模块有独立的js、css文件，通过Openresty提供前端服务。首先会加载nav模块（含index.html），然后nav模块再根据Url路径动态加载其他前端模块。

- 存储设施：MySQL、Redis、RabbitMQ、MinIO、MongoDB。

## 系统设计

## 代码结构

## 前端编译

## 后端编译

## 系统部署

