#/bin/bash

docker stop zookeeper
docker rm zookeeper
cd ~/shared-logger-client
docker-compose up -d zookeeper

sleep 3

docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1/config
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2/config
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1/config/learner_recovery
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2/config/learner_recovery
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology1/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology2/config/learner_recovery 0