#!/bin/bash

ipProposer=pc833.emulab.net
ipAcceptor=pc855.emulab.net
ipLogger1=pc849.emulab.net
ipLogger2=pc834.emulab.net
ipReplica1=pc845.emulab.net
ipReplica2=pc830.emulab.net
ipReplica3=pc831.emulab.net
ipClient=pc822.emulab.net

outputFile=~/code/shared-logger-client/evaluation/thinking_time_50/io_sem_4_500_001/

threads=(2 4 8 16 32 64 128 256 512 1024)
apps=(io cpu)
logs=(sem cou dec)
ips=($ipProposer $ipAcceptor $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3)


ssh lucas123@$ipProposer '~/shared-logger-client/scripts/run_proposer.sh' &
sleep 5
ssh lucas123@$ipAcceptor '~/shared-logger-client/scripts/run_acceptor.sh' &
sleep 5
ssh lucas123@$ipReplica1 "~/shared-logger-client/scripts/run_cpu_sem_replica_1.sh" &
sleep 5
ssh lucas123@$ipReplica2 "~/shared-logger-client/scripts/run_cpu_sem_replica_2.sh" &
sleep 5
ssh lucas123@$ipReplica3 "~/shared-logger-client/scripts/run_cpu_sem_replica_3.sh" &
sleep 5

ssh lucas123@$ipClient "~/shared-logger-client/scripts/run_experiment.sh 2"

./pegarLogs.sh $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile

sleep 5

for ip in "${ips[@]}" 
do 
	ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh' &
done 


sleep 5

ssh lucas123@$ipProposer '~/shared-logger-client/scripts/clean_zookeeper.sh'

