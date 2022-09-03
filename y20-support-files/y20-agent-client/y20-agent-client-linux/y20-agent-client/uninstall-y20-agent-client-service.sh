#!/bin/bash

sudo systemctl disable y20-agent-client.service
sudo rm -f /etc/systemd/system/y20-agent-client.service
sudo systemctl daemon-reload

sudo systemctl stop y20-agent-client.service

sudo rm -rf ./*

exit