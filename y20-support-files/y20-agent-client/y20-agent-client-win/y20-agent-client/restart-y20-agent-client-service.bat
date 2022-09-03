@echo off

start cmd /C "timeout /t 5 && sc start y20-agent-client"

sc stop y20-agent-client

exit