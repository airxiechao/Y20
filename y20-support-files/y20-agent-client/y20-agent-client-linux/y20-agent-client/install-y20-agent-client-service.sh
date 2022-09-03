#!/bin/bash

currentDir=$(dirname "$(realpath $0)")

sudo cat > /etc/systemd/system/y20-agent-client.service << EOF
[Unit]
Description=Y20 agent client service

[Service]
WorkingDirectory=$currentDir
ExecStart=$currentDir/start-y20-agent-client.sh
User=root
Type=Simple
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

chmod +x $currentDir/*.sh
chmod +x $currentDir/jre/linux/bin/*

sudo systemctl daemon-reload
sudo systemctl enable y20-agent-client.service
sudo systemctl start y20-agent-client.service
