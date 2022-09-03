echo upgrade... > ../../logs/upgrade.log

echo copy files... >> ../../logs/upgrade.log

cp -f -v y20-agent-client/*.jar ../../ >> ../../logs/upgrade.log
cp -f -v y20-agent-client/*.sh ../../ >> ../../logs/upgrade.log
cp -f -v y20-agent-client/lib/* ../../lib/ >> ../../logs/upgrade.log
cp -f -v y20-agent-client/conf/cert/* ../../conf/cert/ >> ../../logs/upgrade.log
cp -f -v y20-agent-client/conf/agent-client-jks.yml ../../conf/agent-client-jks.yml >> ../../logs/upgrade.log

echo restart y20-agent-client... >> ../../logs/upgrade.log

sudo systemctl restart y20-agent-client.service

exit