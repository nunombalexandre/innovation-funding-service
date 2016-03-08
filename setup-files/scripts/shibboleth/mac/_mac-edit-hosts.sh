#!/usr/bin/env bash
source _mac-set-docker-vars.sh
cp /etc/hosts /tmp/hostsbackup
ip_address=$(docker-machine ip)
cat /etc/hosts | grep -v 'ifs-local-dev' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts
