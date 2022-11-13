## 1. 启动 ubuntu 20.04
```
apt-get update
export DEBIAN_FRONTEND=noninteractive
echo "Asia/Shanghai" > /etc/timezone
export TZ=Asia/Shanghai
apt-get install wget curl gnupg apt-transport-https lsb-release software-properties-common -y
```

## 2. 安装 openjdk11
```
apt-get install openjdk-11-jdk -y 
```

## 3. 安装 openresty
```
wget -O - https://openresty.org/package/pubkey.gpg | apt-key add -
apt-add-repository "deb http://openresty.org/package/ubuntu $(lsb_release -sc) main" -y
apt-get update
apt-get install openresty -y --no-install-recommends
```

启动  
```
service openresty start
```

## 4. 安装 consul
```
wget -O - https://apt.releases.hashicorp.com/gpg | apt-key add -
apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" -y 
apt-get update
apt-get install consul -y
```

启动  
```
nohup consul agent -server -ui -data-dir=/var/lib/consul -config-dir=/etc/consul.d/ -bootstrap-expect=1 > /dev/null 2>&1 &
```

## 5. 安装 redis
```
add-apt-repository ppa:redislabs/redis -y
apt-get update
apt-get install redis -y
```

配置密码  
```
sed -i "s/# requirepass foobared/requirepass 123456/" /etc/redis/redis.conf
```

启动  
```
service redis-server start
```

## 6. 安装 rabbitmq
```
curl -1sLf "https://keys.openpgp.org/vks/v1/by-fingerprint/0A9AF2115F4687BD29803A206B73A36E6026DFCA" | gpg --dearmor | tee /usr/share/keyrings/com.rabbitmq.team.gpg > /dev/null
curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/gpg.E495BB49CC4BBE5B.key | gpg --dearmor | tee /usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg > /dev/null
curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/gpg.9F4587F226208342.key | gpg --dearmor | tee /usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg > /dev/null

tee /etc/apt/sources.list.d/rabbitmq.list <<EOF
deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main
deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main
deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main
deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main
EOF

apt-get update -y

apt-get install -y erlang-base \
  erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
  erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
  erlang-runtime-tools erlang-snmp erlang-ssl \
  erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl

apt-get install rabbitmq-server -y --fix-missing
```

启动  
```
service rabbitmq-server start
```

配置用户  
```
rabbitmqctl add_user admin 123456
rabbitmqctl set_user_tags admin administrator
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
```

## 7. 安装 mysql
```
apt-get install mysql-server -y
```

启动  
```
service mysql start
```

配置用户  
```
mysql -e "CREATE USER 'admin'@'localhost' IDENTIFIED BY '123456';GRANT ALL ON *.* TO 'admin'@'localhost';"
```

## 8. 安装 mongodb
```
wget -O - https://www.mongodb.org/static/pgp/server-5.0.asc | apt-key add -
apt-add-repository "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse"
apt-get update
apt-get install -y mongodb-org
```

启动  
```
nohup mongod -f /etc/mongod.conf > /dev/null 2>&1 &
配置用户
mongo admin --eval "db.createUser({user: 'admin', pwd: '123456', roles: ['userAdminAnyDatabase', 'readWriteAnyDatabase']})"
```

配置文件  
```
sed -i "s/#security:/security:\n  authorization: enabled/" /etc/mongod.conf
```

重启  
```
pkill mongod
nohup mongod -f /etc/mongod.conf > /dev/null 2>&1 &
```

## 9. 安装 minio
```
cd /usr/bin
wget https://dl.minio.io/server/minio/release/linux-amd64/minio
chmod +x minio
wget https://dl.minio.io/client/mc/release/linux-amd64/mc
chmod +x mc

mkdir /var/lib/minio

export MINIO_ROOT_USER=admin
export MINIO_ROOT_PASSWORD=12345678
nohup minio server /var/lib/minio --address :9000  --console-address :9001 > /dev/null 2>&1 &
```

## 10. 构造程序目录  
```
/opt/y20
  /y20-gateway
  /y20-config
  /y20-frontend
  /y20-backend
```

## 11. 部署 y20-gateway
上传y20-gateway到/opt/y20/y20-gateway  

删除openresty原来的conf  
```
rm -rf  /usr/local/openresty/nginx/conf
```
连接y20-gateway的conf
```
ln -s /usr/local/openresty/nginx/conf /opt/y20/y20-gateway/conf
```
修改前端目录路径  
/y20/y20-gateway/conf/lua/init.lua 中修改为  static_dir = "/opt/y20/y20-frontend"  

添加https证书  
```
cd /y20/y20-gateway/conf/cert 
``` 

安装 mkcert
```
curl -s https://api.github.com/repos/FiloSottile/mkcert/releases/latest \
| grep "browser_download_url.*linux-amd64" \
| cut -d : -f 2,3 | tr -d \" \
| wget -i - -O mkcert
chmod +x  mkcert
mv mkcert /usr/bin
mkcert 127.0.0.1
```
/y20/y20-gateway/conf/server.conf 中修改为
```
ssl_certificate cert/127.0.0.1.pem;  
ssl_certificate_key cert/127.0.0.1-key.pem;  
```
/y20/y20-gateway/conf/nginx.conf 的 lua_package_path 改为绝对路径  

/y20/y20-gateway/conf/lua/router_srv_req_rate_limit_in.lua 里面的路径改为绝对路径  

重启openresty
```
service openresty restart
```

## 12. 部署 y20-frontend
上传y20-gateway到/opt/y20/y20-frontend

## 13. 部署 y20-config
在cert文件夹生成节点RPC用的证书  

修改各个配置文件  

生成 auth-common.yml 的 serviceAccessToken  

生成 minio.yml 的 minio的accessKey和secretKey  
```
mc alias set myminio http://127.0.0.1:9000 admin 12345678
mc admin user svcacct add myminio admin
```

## 14. 初始化 mysql、mongodb、minio
创建mysql数据库 sql-create  
初始化mysql数据 sql-initialize  
创建mongodb数据库 mongodb-create  
创建minio的bucket
```
mc mb myminio/y20-artifact  
```

## 15. 部署 y20-backend
上传y20-backend到/opt/y20/y20-backend

## 16. 部署 agent 安装包、更新包
将安装包、升级包放入 /y20-frontend/agent-client-release  

发布agent版本
```
mysql -e "insert into y20_agent.agent_version (id,version,release_time,download_linux_url,download_linux_upgrader_url,download_win_url,download_win_upgrader_url) values (1,'v0.6.SNAPSHOT.a4d54c8','2022-11-07 22:15:01','https://127.0.0.1/agent-client-release/y20-agent-client-linux.zip','https://127.0.0.1/agent-client-release/y20-agent-client-upgrader-linux.zip','https://127.0.0.1/agent-client-release/y20-agent-client-win.zip','https://127.0.0.1/agent-client-release/y20-agent-client-upgrader-win.zip');"
```

## 17. 新建后端服务启动脚本
在 /opt/y20/y20-backend 新建 start-all.sh
```
cd auth
sudo ./start-auth.sh
cd -

cd project
sudo ./start-project.sh
cd -

cd pipeline
sudo ./start-pipeline.sh
cd -

cd pipeline-run-instance
sudo ./start-pipeline-run-instance.sh
cd -

cd agent
sudo ./start-agent.sh
cd -

cd agent-server
sudo ./start-agent-server.sh
cd -

cd activity
sudo ./start-activity.sh
cd -

cd artifact
sudo ./start-artifact.sh
cd -

cd cron
sudo ./start-cron.sh
cd -

cd email
sudo ./start-email.sh
cd -

cd ip
sudo ./start-ip.sh
cd -

cd log
sudo ./start-log.sh
cd -

cd manmachinetest
sudo ./start-manmachinetest.sh
cd -

cd monitor
sudo ./start-monitor.sh
cd -

cd pay
sudo ./start-pay.sh
cd -

cd quota
sudo ./start-quota.sh
cd -

cd sms
sudo ./start-sms.sh
cd -

cd template
sudo ./start-template.sh
cd -
```

## 18. 启动所有服务和程序
```
echo 'start ssh...'
service ssh start
echo 'start openresty...'
service openresty start
echo 'start redis...'
service redis-server start
echo 'start rabbitmq...'
service rabbitmq-server start
echo 'start mysql...'
service mysql start

cd /opt
echo 'start consul...'
nohup consul agent -server -ui -data-dir=/var/lib/consul -config-dir=/etc/consul.d/ -bootstrap-expect=1 > /dev/null 2>&1 &
echo 'start mongodb...'
nohup mongod -f /etc/mongod.conf > /dev/null 2>&1 &
echo 'start minio...'
export MINIO_ROOT_USER=admin
export MINIO_ROOT_PASSWORD=12345678
nohup minio server /var/lib/minio --address :9000  --console-address :9001 > /dev/null 2>&1 &

cd /opt/y20/y20-backend
echo 'start backend...'
./start-all.sh
```