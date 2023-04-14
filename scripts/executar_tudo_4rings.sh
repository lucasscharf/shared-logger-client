#!/bin/bash

sun=node0
earth=node1
venus=node2
mars=node3
jupyter=node4
#node5
uranus=node5
neptune=node6


apps=(cpu)
logs=(sem)
threads=(128 256 512 1024 2048)

ips=($earth $venus $mars $jupyter $neptune $uranus)

cd ~/shared-logger-client/scripts 

for ip in "${ips[@]}" 
do
	ssh lucas123@$ip 'sudo rm -rf /tmp/* ; sudo chmod 777 /tmp ; sudo chown -R lucas123 /media ; sudo rm -rf /media/disk1/* ; sudo rm -rf /media/disk2/* ; sudo rm -rf /media/disk3/* ; sudo rm -rf /media/disk4/* ; sudo rm -rf /media/disk5/* ; sudo rm -rf /media/disk6/*'
done 

for app in "${apps[@]}" 
do
	for log in "${logs[@]}"
	do
		for thread in "${threads[@]}"
		do
			if [ $log = dec ]; then
				ssh lucas123@$neptune "~/shared-logger-client/scripts/run_quarkus_simple.sh" &
				sleep 2
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
			
			ssh lucas123@$uranus "~/shared-logger-client/scripts/run_$app\_$log\_replica_2.sh" &
			ssh lucas123@$uranus "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_2.sh" &
			ssh lucas123@$neptune "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_3.sh" &
			ssh lucas123@$neptune "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_4.sh" &

			sleep 5

			echo "Executando o experimento App: [$app] Tipo de Log: [$log] # de Threads: [$thread] "
			~/shared-logger-client/scripts/run_experiment.sh $thread
			echo "Killing sshs"
			killall -9 ssh

			if [ $log = dec ]; then
				ssh lucas123@$uranus "curl -XPOST localhost:8888/closeLoggers" 
				ssh lucas123@$neptune "curl -XPOST localhost:8888/closeLoggers" 
			fi

			echo "Matando os javas"
			for ip in "${ips[@]}" 
			do 
				ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
			done 

			echo "Getting logs"
			~/shared-logger-client/scripts/recovery_logs_4rings.sh $sun $earth $venus $mars $jupyter $neptune $uranus $outputFile

			echo "Rebuilding zookeeper"
			~/shared-logger-client/scripts/clean_zookeeper.sh > /dev/null
			killall -9 ssh
		done
	done
done

echo "Fim de execução"


#a=$(ps aux | grep java | grep trunk | cut -d " " -f 5 | awk '{s=$0} NR==1  {printf "tail -f /proc/%s/fd/1",s}');
#echo $a ;
#echo $a | bash

