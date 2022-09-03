#!/bin/bash

currentDir=$(dirname "$(realpath $0)")

$currentDir/jre/linux/bin/java -Dfile.encoding=utf-8 -jar boot-agent-client.jar
