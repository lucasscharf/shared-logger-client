#!/bin/bash

killall -9 java
cd ~/shared-logger-client/server
mvn quarkus:dev > /tmp/execucao &
sleep 10
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":4,\"pathPrefix\":\"/tmp/\",\"replicas\":2,\"ring\":\"1\"}"