#!/bin/bash

latencyPath="/tmp/aaaaaa"
outputFile="~/code/shared-logger-client/evaluation/thinking_time_50/io_des_64_500_001/"

mkdir $outputFile

ipLogger1=
ipLogger2=
ipReplica1=
ipReplica2=
ipReplica3=
ipClient=

scp lucas123@$ipClient:$latencyPath $outputFile/client_latency.csv
scp lucas123@$ipClient:/tmp/execucao $outputFile/client_latency.csv
scp lucas123@$ipLogger1:/tmp/execucao $outputFile/logger_1.csv
scp lucas123@$ipLogger2:/tmp/execucao $outputFile/logger_2.csv

scp lucas123@$ipReplica1:/tmp/execucao $outputFile/replica_1.csv
scp lucas123@$ipReplica2:/tmp/execucao $outputFile/replica_2.csv
scp lucas123@$ipReplica3:/tmp/execucao $outputFile/replica_3.csv