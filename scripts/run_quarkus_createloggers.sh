#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node5:8888,http://node6:8888

java -jar ~/shared-logger-client/server/target/shared-logger-1.0.0-SNAPSHOT-runner.jar > /tmp/execucaoLogger &

sleep 5
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":4,\"pathPrefix\":\"/media/disk1/\",\"replicas\":2,\"ring\":\"1\",\"trackerNumber\":1000}"
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":5,\"pathPrefix\":\"/media/disk2/\",\"replicas\":2,\"ring\":\"2\",\"trackerNumber\":1000}"
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":6,\"pathPrefix\":\"/media/disk3/\",\"replicas\":2,\"ring\":\"3\",\"trackerNumber\":1000}"
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":7,\"pathPrefix\":\"/media/disk4/\",\"replicas\":2,\"ring\":\"4\",\"trackerNumber\":1000}"
