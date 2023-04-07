#!/bin/bash

sun=$1
earth=$2
venus=$3
mars=$4
jupyter=$5
neptune=$6
uranus=$7
outputFile=$8

mkdir -p $outputFile

echo "Sun: $sun"
echo "Uranus: $uranus"
echo "Jupyter: $jupyter"

scp lucas123@$sun:/tmp/execucao_1 /tmp/client.log
latencyPath=$(cat /tmp/client.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_1.csv

scp lucas123@$sun:/tmp/execucao_2 /tmp/client_ring_2.log
latencyPath=$(cat /tmp/client_ring_2.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_2.csv

scp lucas123@$sun:/tmp/execucao_3 /tmp/client_ring_3.log
latencyPath=$(cat /tmp/client_ring_3.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_3.csv

scp lucas123@$sun:/tmp/execucao_4 /tmp/client_ring_4.log
latencyPath=$(cat /tmp/client_ring_4.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$sun:$latencyPath $outputFile/client_latency_4.csv

scp lucas123@$sun:/tmp/execucao_1 $outputFile/client_ring_1.log
scp lucas123@$sun:/tmp/execucao_2 $outputFile/client_ring_2.log
scp lucas123@$sun:/tmp/execucao_3 $outputFile/client_ring_3.log
scp lucas123@$sun:/tmp/execucao_4 $outputFile/client_ring_4.log

scp lucas123@$neptune:/tmp/execucaoLogger $outputFile/logger_1.csv
scp lucas123@$neptune:/tmp/loggerStatsOutdated $outputFile/logger_1_latency.csv
scp lucas123@$uranus:/tmp/execucaoLogger $outputFile/logger_2.csv
scp lucas123@$uranus:/tmp/loggerStatsOutdated $outputFile/logger_2_latency.csv

scp lucas123@$jupyter:/tmp/execucao_1 $outputFile/replica_1_ring_1.csv
scp lucas123@$jupyter:/tmp/execucao_2 $outputFile/replica_1_ring_2.csv
scp lucas123@$jupyter:/tmp/execucao_3 $outputFile/replica_1_ring_3.csv
scp lucas123@$jupyter:/tmp/execucao_4 $outputFile/replica_1_ring_4.csv

scp lucas123@$uranus:/tmp/execucao_1 $outputFile/replica_2_ring_1.csv
scp lucas123@$uranus:/tmp/execucao_2 $outputFile/replica_2_ring_2.csv
scp lucas123@$neptune:/tmp/execucao_3 $outputFile/replica_2_ring_3.csv
scp lucas123@$neptune:/tmp/execucao_4 $outputFile/replica_2_ring_4.csv

