#!/bin/bash

ipProposer=pc833.emulab.net
ipAcceptor=pc855.emulab.net
ipLogger1=pc788.emulab.net
ipLogger2=pc784.emulab.net
ipReplica1=pc790.emulab.net
ipReplica2=pc793.emulab.net
ipReplica3=pc789.emulab.net
ipClient=pc795.emulab.net

outputFile=~/code/shared-logger-client/evaluation/thinking_time_50/io_sem_4_500_001/

threads=(2 4 8 16 32 64 128 256 512 1024)
ips=($ipProposer $ipAcceptor $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3)

ssh lucas123@ipProposer ~/shared-logger-client/scripts/run_proposer.sh

for thread in "${threads[@]}" 
do
	echo "$thread"
done 

for ip in "${ips[@]}" 
do 
	echo $ip
done 
#./pegarLogs $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile
