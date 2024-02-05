#!/bin/bash
#this script should be ran when we use simple ubuntu without Java

cd ~
git clone https://github.com/lucasscharf/shared-logger-client
cd shared-logger-client
git pull

sudo apt update
sudo apt install openjdk-17-jdk maven -y
sudo apt-get remove docker docker-engine docker.io containerd runc

sudo apt-get update
sudo apt-get install -y \
	ca-certificates \
	curl \
	gnupg \
	lsb-release

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo \
"deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg trusted=yes] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose

sudo newgrp docker
sudo chmod 666 /var/run/docker.sock
sudo usermod -aG docker ${USER}

cd ~/shared-logger-client
docker-compose up -d zookeeper