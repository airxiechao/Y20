@echo off

set "currentDir=%~dp0"

cd /d %currentDir%

start cmd /C "timeout /t 5 && rmdir /S/Q ."

nssm\nssm remove y20-agent-client confirm
nssm\nssm stop y20-agent-client

exit
