@echo off

set "currentDir=%~dp0"

cd /d %currentDir%

nssm\nssm install y20-agent-client "%currentDir%start-y20-agent-client.bat"

nssm\nssm start y20-agent-client

pause
