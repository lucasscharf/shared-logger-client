#!/bin/bash

killall -9 java

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node8:8888,http://node7:8888

cd ~/shared-logger-client/server
mvn quarkus:dev > /tmp/execucao &
sleep 10
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":4,\"pathPrefix\":\"/tmp/\",\"replicas\":2,\"ring\":\"1\"}"