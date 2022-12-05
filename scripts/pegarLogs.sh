#!/bin/bash


ipLogger1=$1
ipLogger2=$2
ipReplica1=$3
ipReplica2=$4
ipReplica3=$5
ipClient=$6
outputFile=$7


mkdir $outputFile

scp lucas123@$ipClient:/tmp/execucao /tmp/client.log
latencyPath=$(cat /tmp/client.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$ipClient:$latencyPath $outputFile/client_latency.csv

scp lucas123@$ipClient:/tmp/execucao $outputFile/client.log
scp lucas123@$ipLogger1:/tmp/execucao $outputFile/logger_1.csv
scp lucas123@$ipLogger2:/tmp/execucao $outputFile/logger_2.csv

scp lucas123@$ipReplica1:/tmp/execucao $outputFile/replica_1.csv
scp lucas123@$ipReplica2:/tmp/execucao $outputFile/replica_2.csv
scp lucas123@$ipReplica3:/tmp/execucao $outputFile/replica_3.csv