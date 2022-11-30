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
apps=(io cpu)
logs=(sem cou dec)
ips=($ipProposer $ipAcceptor $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3)


ssh lucas123@$ipProposer '~/shared-logger-client/scripts/run_proposer.sh &' &
ssh lucas123@$ipAcceptor '~/shared-logger-client/scripts/run_acceptor.sh &' &
ssh lucas123@$ipReplica1 "~/shared-logger-client/scripts/run_cpu_sem_replica_1.sh &" &
ssh lucas123@$ipReplica2 "~/shared-logger-client/scripts/run_cpu_sem_replica_2.sh &" &
ssh lucas123@$ipReplica3 "~/shared-logger-client/scripts/run_cpu_sem_replica_3.sh &" &

sleep 3

ssh lucas123@$ipClient "~/shared-logger-client/scripts/run_experiment.sh"

./pegarLogs $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile

for ip in "${ips[@]}" 
do 
	ssh lucas123@$ipProposer '~/shared-logger-client/scripts/kill_all_java.sh &' &
done 

