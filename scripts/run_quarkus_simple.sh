#/!bin/bash

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node8:8888,http://node7:8888

killall -9 java
cd ~/shared-logger-client/server
mvn quarkus:dev > /tmp/execucao
