#!/bin/bash

ipZookeeper=pc822.emulab.net
ipProposer=pc731.emulab.net
ipAcceptor=pc829.emulab.net
ipLogger1=pc726.emulab.net
ipLogger2=pc725.emulab.net
ipReplica1=pc738.emulab.net
ipReplica2=pc740.emulab.net
ipReplica3=pc831.emulab.net
ipClient=pc825.emulab.net

outputFile=~/code/shared-logger-client/evaluation/thinking_time_50/io_sem_4_500_001/

threads=(2 4 8 16 32 64 128 256 512 1024)
apps=(io cpu)
logs=(sem cou dec)
ips=($ipProposer $ipAcceptor $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3)


ssh lucas123@$ipProposer '~/shared-logger-client/scripts/run_proposer.sh' & 
ssh lucas123@$ipAcceptor '~/shared-logger-client/scripts/run_acceptor.sh' & 
ssh lucas123@$ipReplica1 "~/shared-logger-client/scripts/run_cpu_sem_replica_1.sh" &
ssh lucas123@$ipReplica2 "~/shared-logger-client/scripts/run_cpu_sem_replica_2.sh" &
ssh lucas123@$ipReplica3 "~/shared-logger-client/scripts/run_cpu_sem_replica_3.sh" &
echo "hora do sleep"
sleep 30

# echo "acordar do sleep"
# ssh lucas123@$ipClient "~/shared-logger-client/scripts/run_experiment.sh 2"

# ./pegarLogs.sh $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile

for ip in "${ips[@]}" 
do 
	ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh' &
done 

sleep 5

# ssh lucas123@$ipZookeeper '~/shared-logger-client/scripts/clean_zookeeper.sh'

