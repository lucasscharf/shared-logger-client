#/!bin/bash

export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64/
export nodes_replicas_url=http://node8:8888,http://node7:8888

java -jar ~/shared-logger-client/server/target/shared-logger-1.0.0-SNAPSHOT-runner.jar > /tmp/execucao
