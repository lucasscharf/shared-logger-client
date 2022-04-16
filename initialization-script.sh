#!/bin/bash

cd ~
git clone https://github.com/lucasscharf/shared-logger-client

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node0:8888,http://node5:8888

sudo apt-get remove docker docker-engine docker.io containerd runc

sudo apt-get update
sudo apt-get install -y \
	ca-certificates \
	curl \
	gnupg \
	lsb-release

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo \
"deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose

sudo newgrp docker
sudo chmod 666 /var/run/docker.sock
sudo usermod -aG docker ${USER}

exit 

cd ~/shared-logger-client
docker-compose up -d zookeeper

cd ~/shared-logger-client/URingPaxos/
mvn clean install -DskipTests

cd ~/shared-logger-client/URingPaxos/target
unzip Paxos-trunk-release.zip

cd ~/shared-logger-client/URingPaxos/clients/SMR 
mvn clean install -DskipTests

cd ~/shared-logger-client/URingPaxos/clients/SMR/target 
unzip SMR-trunk-release.zip

cd ~/shared-logger-client/server/
mvn clean install