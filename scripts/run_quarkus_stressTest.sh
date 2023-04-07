#!/bin/bash

threadCount=$1
ringCount=$2

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node5:8888,http://node4:8888

java -jar ~/shared-logger-client/server/target/shared-logger-1.0.0-SNAPSHOT-runner.jar > /tmp/execucaoLogger &

sleep 5
curl -X POST http://localhost:8888/executeLoggerPerformanceTest/$threadCount/$ringCount

killall -9 java
