#!/bin/bash

sun=pc783.emulab.net
earth=pc798.emulab.net
venus=pc799.emulab.net
mars=pc793.emulab.net
jupyter=pc724.emulab.net
#node5
uranus=pc716.emulab.net
saturn=pc714.emulab.net


apps=(cpu)
logs=(sem)
threads=(4)

ips=($earth $venus $mars $jupyter $saturn $uranus)

cd ~/shared-logger-client/scripts 

for ip in "${ips[@]}" 
do
	ssh lucas123@$ip 'sudo rm -rf /tmp/* ; sudo chown lucas123 /media ; sudo mkdir /media/disk1 ; sudo rm -rf /media/disk1/*'
done 

for ip in "${ips[@]}" 
do 
	ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
done 

for app in "${apps[@]}" 
do
	for log in "${logs[@]}"
	do
		for thread in "${threads[@]}"
		do
			if [ $log = dec ]; then
				ssh lucas123@$uranus "~/shared-logger-client/scripts/run_quarkus_createloggers.sh" &
				sleep 6
			fi

			outputFile=~/shared-logger-client/evaluation/thinking_time_50/4rings/$app\_$log\_$thread\_90_001/

			ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer.sh' & 
			ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_2.sh' & 
			ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_3.sh' & 
			ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_4.sh' & 

			ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor.sh' & 
			ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_2.sh' & 
			ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_3.sh' & 
			ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_4.sh' & 

			ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1.sh" &
			ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_2.sh" &
			ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_3.sh" &
			ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_4.sh" &

			sleep 5

			echo "Executando o experimento App: [$app] Tipo de Log: [$log] # de Threads: [$thread] "
			~/shared-logger-client/scripts/run_experiment.sh $thread
			echo "Killing sshs"
			killall -9 ssh

			if [ $log = dec ]; then
				ssh lucas123@$uranus "curl -XPOST localhost:8888/closeLoggers" 
			fi

			echo "Matando os javas"
			for ip in "${ips[@]}" 
			do 
				ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
			done 

			echo "Getting logs"
			~/shared-logger-client/scripts/recovery_logs_4rings.sh $sun $earth $venus $mars $jupyter $saturn $uranus $outputFile

			# echo "Cleaning tmps"
			# for ip in "${ips[@]}" 
			# do 
			# 	echo "Cleaning ip: $ip"
			# 	ssh lucas123@$ip '~/shared-logger-client/scripts/clean_tmp.sh'
			# done 

			echo "Rebuilding zookeeper"
			~/shared-logger-client/scripts/clean_zookeeper.sh > /dev/null
			killall -9 ssh
		done
	done
done

echo "Fim de execução"


