#/!bin/bash

killall -9 java
cd ~/shared-logger-client/server
mvn quarkus:dev > /tmp/execucao
