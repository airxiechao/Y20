echo upgrade... > ..\..\logs\upgrade.log

echo copy files... >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\*.jar ..\..\ >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\*.bat ..\..\ >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\lib ..\..\lib >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\conf\cert ..\..\conf\cert >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\conf\agent-client-jks.yml ..\..\conf\ >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\conf\*.xml ..\..\conf\ >> ..\..\logs\upgrade.log
copy /Y y20-agent-client\conf\*.properties ..\..\conf\ >> ..\..\logs\upgrade.log

echo restart y20-agent-client... >> ..\..\logs\upgrade.log

start cmd /C "timeout /t 5 && sc start y20-agent-client"

sc stop y20-agent-client >> ..\..\logs\upgrade.log

exit