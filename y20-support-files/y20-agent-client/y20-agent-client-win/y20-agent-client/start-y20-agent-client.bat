@echo off

set "currentDir=%~dp0"

title y20-agent-client

%currentDir%/jre/windows/bin/java -Dfile.encoding=utf-8 -jar boot-agent-client.jar
