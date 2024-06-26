## 系统组织
整个系统由前端模块、后端微服务、节点agent程序、微服务中间件和各种存储设施组成。

- 微服务中间件：使用Consul作为注册注册中心，Openresty作为网关。Openresty通过Lua脚本，根据请求Url动态查询从Consul查询相关服务的地址，然后转发。

- 后端服务：各个独立运行的Rest、Rpc服务，注册到Consul。每个服务都有自己的配置文件和独立的数据库。

- 节点agent：用于连接远程节点和后端服务的程序。

- 前端模块：前端分模块，每个模块有独立的js、css文件，通过Openresty提供前端服务。首先会加载nav模块（含index.html），然后nav模块再根据Url路径动态加载其他前端模块。

- 存储：MySQL、Redis、RabbitMQ、MinIO、MongoDB、InfluxDB。

## 后端代码
后端按微服务划分模块：
- allinone 所有服务的整合
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

## 后端编译（JDK11）
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

4. 节点agent编译好后，需要和 `Y20/y20-support-files/y20-agent-client` 相关内容打包在一起，形成安装包和更新包


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

## 前端编译（Node16）
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

1. 目录规划
```
y20 （主目录）
  - y20-fontend
    - home
    - ...
    - （前端各个子项目）
  - y20-backend
    - project
    - ...
    - （后端各个微服务—）
  - y20-config 后端微服务配置
  - y20-gateway
    - conf Openresty的conf文件夹（通过符号链接引用）
```

2. 安装 Openresty
    - 用 `Y20/y20-gateway/conf` 替换Openresty的 `conf` 文件夹。

    - 在 `conf/cert` 中，放入HTTPS证书，修改 `conf/server.conf` 中对应的 *ssl_certificate* 和 *ssl_certificate_key* 配置。

    - 在 `conf/lua/init.lua` 中，修改 *static_dir* 为前端编译后的文件夹。

3. 安装 Consul、Redis、RabbitMQ、MySQL、MongoDB、InfluxDB、MinIO
    - 拷贝 `Y20/y20-config` 到部署目录， 修改各个配置文件的相关内容。

4. 拷贝程序并启动
    - 前端程序拷贝到 `y20-frontend`
    - 后端程序拷贝到 `y20-backend`
    - 启动后端allinone服务，比如
    
      ```
      cd y20/y20-backend/allinone
      nohup java -jar boot-allinone.jar > /dev/null 2>&1 &
      ```

## Docker 镜像（待更新）
```
docker run -itd -p 30022:22 -p 80:80 -p 443:443 -p 9100:9100 airxiechao/y20
```

[镜像制作方法](MakeDockerImage.md) 