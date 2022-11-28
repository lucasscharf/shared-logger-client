#!/bin/bash

outputFile=~/code/shared-logger-client/evaluation/thinking_time_50/io_sem_4_500_001/

ipLogger1=pc788.emulab.net
ipLogger2=pc784.emulab.net
ipReplica1=pc790.emulab.net
ipReplica2=pc793.emulab.net
ipReplica3=pc789.emulab.net
ipClient=pc795.emulab.net


mkdir $outputFile

scp lucas123@$ipClient:/tmp/execucao /tmp/client.log
latencyPath=$(cat /tmp/client.log | grep "Raw path" | cut -d'[' -f3  | cut -d']' -f1 | cut -d' ' -f2 )
scp lucas123@$ipClient:$latencyPath $outputFile/client_latency.csv

scp lucas123@$ipClient:/tmp/execucao $outputFile/client.log
#scp lucas123@$ipLogger1:/tmp/execucao $outputFile/logger_1.csv
#scp lucas123@$ipLogger2:/tmp/execucao $outputFile/logger_2.csv

scp lucas123@$ipReplica1:/tmp/execucao $outputFile/replica_1.csv
scp lucas123@$ipReplica2:/tmp/execucao $outputFile/replica_2.csv
scp lucas123@$ipReplica3:/tmp/execucao $outputFile/replica_3.csv