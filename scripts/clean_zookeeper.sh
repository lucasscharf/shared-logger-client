#/bin/bash

docker rm -f zookeeper
cd ~/shared-logger-client
docker-compose up -d zookeeper

sleep 3

docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology3 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology4 0

docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1/config 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2/config 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology3/config 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology4/config 0

docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology1/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology2/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology3/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh create /ringpaxos/topology4/config/learner_recovery 0

docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology1/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology2/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology3/config/learner_recovery 0
docker exec zookeeper /opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology4/config/learner_recovery 0