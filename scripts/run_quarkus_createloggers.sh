#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node8:8888,http://node7:8888

java -jar ~/shared-logger-client/server/target/shared-logger-1.0.0-SNAPSHOT-runner.jar > /tmp/execucao &

sleep 5
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":4,\"pathPrefix\":\"/media/disk1/\",\"replicas\":1,\"ring\":\"1\"}"
