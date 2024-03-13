#!/bin/bash

ipLogger1=$1
ipLogger2=$2
ipReplica1=$3
ipReplica2=$4
ipReplica3=$5
ipClient=$6
outputFile=$7
ipReplica1_ring_2=$8
ipReplica2_ring_2=$9
ipReplica3_ring_2=${10}

mkdir $outputFile

scp lucas123@$ipClient:/tmp/execucao /tmp/client.log
latencyPath=$(cat /tmp/client.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$ipClient:$latencyPath $outputFile/client_latency.csv

scp lucas123@$ipClient:/tmp/execucao_2 /tmp/client_ring_2.log
latencyPath=$(cat /tmp/client_ring_2.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$ipClient:$latencyPath $outputFile/client_latency_2.csv

scp lucas123@$ipClient:/tmp/execucao $outputFile/client.log
scp lucas123@$ipClient:/tmp/execucao_2 $outputFile/client_ring_2.log
#scp lucas123@$ipLogger1:/tmp/execucaoLogger $outputFile/logger_1.csv
#scp lucas123@$ipLogger1:/tmp/loggerStatsOutdated $outputFile/logger_latency.csv
scp lucas123@$ipLogger2:/tmp/execucaoLogger $outputFile/logger_2.csv
scp lucas123@$ipLogger2:/tmp/loggerStatsOutdated $outputFile/logger_latency.csv

scp lucas123@$ipReplica1:/tmp/execucao $outputFile/replica_1.csv
scp lucas123@$ipReplica2:/tmp/execucao $outputFile/replica_2.csv
scp lucas123@$ipReplica3:/tmp/execucao $outputFile/replica_3.csv

scp lucas123@$ipReplica1_ring_2:/tmp/execucao $outputFile/replica_1_ring_2.csv
scp lucas123@$ipReplica2_ring_2:/tmp/execucao $outputFile/replica_2_ring_2.csv
scp lucas123@$ipReplica3_ring_2:/tmp/execucao $outputFile/replica_3_ring_2.csv
