#!/bin/bash

$sun=$1
$earth=$2
$venus=$3
$mars=$4
$jupyter=$5
$saturn=$6
$uranus=$7
outputFile=$8

mkdir $outputFile

scp lucas123@$sun:/tmp/execucao /tmp/client.log
latencyPath=$(cat /tmp/client.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency.csv

scp lucas123@$sun:/tmp/execucao_2 /tmp/client_ring_2.log
latencyPath=$(cat /tmp/client_ring_2.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_2.csv

scp lucas123@$sun:/tmp/execucao_3 /tmp/client_ring_3.log
latencyPath=$(cat /tmp/client_ring_3.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_3.csv

scp lucas123@$sun:/tmp/execucao_4 /tmp/client_ring_4.log
latencyPath=$(cat /tmp/client_ring_4.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_4.csv

scp lucas123@$sun:/tmp/execucao $outputFile/client.log
scp lucas123@$sun:/tmp/execucao_2 $outputFile/client_ring_2.log
scp lucas123@$sun:/tmp/execucao_3 $outputFile/client_ring_3.log
scp lucas123@$sun:/tmp/execucao_4 $outputFile/client_ring_4.log

scp lucas123@$uranus:/tmp/execucao $outputFile/logger_2.csv
scp lucas123@$uranus:/tmp/loggerStats $outputFile/logger_latency.csv

#scp lucas123@$ipLogger1:/tmp/execucao $outputFile/logger_1.csv
#scp lucas123@$ipLogger1:/tmp/loggerStats $outputFile/logger_latency.csv
scp lucas123@$ipLogger2:/tmp/loggerStats $outputFile/logger_latency.csv

scp lucas123@$ipReplica1:/tmp/execucao $outputFile/replica_1.csv
scp lucas123@$ipReplica2:/tmp/execucao $outputFile/replica_2.csv
scp lucas123@$ipReplica3:/tmp/execucao $outputFile/replica_3.csv

scp lucas123@$ipReplica1_ring_2:/tmp/execucao $outputFile/replica_1_ring_2.csv
scp lucas123@$ipReplica2_ring_2:/tmp/execucao $outputFile/replica_2_ring_2.csv
scp lucas123@$ipReplica3_ring_2:/tmp/execucao $outputFile/replica_3_ring_2.csv
