# FAQs

## 1. 流水线脚本步骤执行 `apt install -y` 会卡住？
Linux的脚本步骤默认通过 `pty4j` 启动 `bash` 进程执行。进程启动时，命令会一次性全部输入到终端输入流，终端再一行一行的执行命令。当某些命令（比如：`apt install`）执行时间比较长时，终端依然在读入命令，导致后面的命令会被忽略。

有以下几种解决办法：
- 在相关命令后加 `< /dev/null`，禁止命令读取输入
- 在每行命令后加 `\`（包括最后一行），把所有命令连接成一行

## 2. Windows主机运行节点接入脚本（ps1）报 `UnauthorizedAccess` 错误？
Windows的安全策略可能阻止从网络下载的 `ps1` 脚本运行。

解决办法：
- 以管理员身份打开 `Powershell` 终端
- 执行 `set-executionpolicy bypass`，更改脚本运行策略

