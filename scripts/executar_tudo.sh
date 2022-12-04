#!/bin/bash

ipZookeeper=pc822.emulab.net
ipProposer=pc731.emulab.net
ipAcceptor=pc725.emulab.net
ipReplica1=pc738.emulab.net
ipReplica2=pc740.emulab.net
ipReplica3=pc831.emulab.net
ipClient=pc726.emulab.net
ipLogger1=pc825.emulab.net
ipLogger2=pc829.emulab.net

threads=(2 4 8 16 32 64 128 256 512 1024)
apps=(io cpu)
logs=(sem cou dec)
ips=($ipReplica1 $ipLogger1 $ipProposer $ipAcceptor $ipLogger2 $ipReplica2 $ipReplica3)

for log in "${logs[@]}"
do
	for app in "${apps[@]}" 
	do
		for thread in "${threads[@]}"
		do
			outputFile=~/code/shared-logger-client/evaluation/thinking_time_50/$app\_$log\_$thread\_90_001/
			ssh lucas123@$ipProposer '~/shared-logger-client/scripts/run_proposer.sh > /dev/null' & 
			ssh lucas123@$ipAcceptor '~/shared-logger-client/scripts/run_acceptor.sh > /dev/null' & 
			ssh lucas123@$ipReplica1 "~/shared-logger-client/scripts/run_$app\_$log\_replica_1.sh" &
			ssh lucas123@$ipReplica2 "~/shared-logger-client/scripts/run_$app\_$log\_replica_2.sh" &
			ssh lucas123@$ipReplica3 "~/shared-logger-client/scripts/run_$app\_$log\_replica_3.sh" &
			
			if [ $log = dec ]; then
				ssh lucas123@$ipLogger1 "~/shared-logger-client/scripts/run_quarkus_simple.sh" &
				ssh lucas123@$ipLogger2 "~/shared-logger-client/scripts/run_quarkus_createloggers.sh" &
			fi
			sleep 5

			ssh lucas123@$ipClient "~/shared-logger-client/scripts/run_experiment.sh $thread"

			for ip in "${ips[@]}" 
			do 
				ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh' 
			done 

			./pegarLogs.sh $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile

			for ip in "${ips[@]}" 
			do 
				ssh lucas123@$ip '~/shared-logger-client/scripts/clean_tmp.sh' & 
			done 
			sleep 5

			ssh lucas123@$ipZookeeper '~/shared-logger-client/scripts/clean_zookeeper.sh >> /dev/null'
			killall -9 ssh
		done
	done
done