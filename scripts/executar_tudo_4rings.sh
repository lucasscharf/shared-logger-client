#!/bin/bash

sun=node0
earth=node1
venus=node2
mars=node3
jupyter=node4
#loggers
uranus=node5
neptune=node6

number_of_experiments=1

experiments=(2)
apps=(io)
logs=(dec)
threads=(8)
thinking_time=3

ips=($earth $venus $mars $jupyter $neptune $uranus)

cd ~/shared-logger-client/scripts 

for ip in "${ips[@]}" 
do
	ssh lucas123@$ip 'sudo chmod 777 /tmp ;  rm -rf /tmp/* ; sudo chown -R lucas123 /media ; rm -rf /media/disk1/* ; rm -rf /media/disk2/* ; rm -rf /media/disk3/* ; rm -rf /media/disk4/* ; rm -rf /media/disk5/* ; rm -rf /media/disk6/*'
done 

for experiment in "${experiments[@]}"
do 
	for app in "${apps[@]}" 
	do
		for log in "${logs[@]}"
		do
			for thread in "${threads[@]}"
			do
				number_of_experiments=$experiment; 
				
				if [ $log = dec ]; then
					ssh lucas123@$neptune "~/shared-logger-client/scripts/run_quarkus_simple.sh" &
					sleep 2
					ssh lucas123@$uranus "~/shared-logger-client/scripts/run_quarkus_createloggers.sh" &
					sleep 2
				fi

				outputFile=~/shared-logger-client/evaluation/thinking_time_$thinking_time/$number_of_experiments\rings/$app\_$log\_$thread\_90_001/

				ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer.sh' & 
				ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor.sh' & 
				ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1.sh" &
				ssh lucas123@$uranus "~/shared-logger-client/scripts/run_$app\_$log\_replica_2.sh" &
				
				if [ "$number_of_experiments" -ge "2" ]; then
					ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_2.sh' & 

					ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_2.sh' & 

					ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_2.sh" &

					ssh lucas123@$uranus "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_2.sh" &
				fi

				if [ "$number_of_experiments" -ge "4" ]; then
					ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_3.sh' & 
					ssh lucas123@$venus '~/shared-logger-client/scripts/run_proposer_ring_4.sh' & 

					ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_3.sh' & 
					ssh lucas123@$earth '~/shared-logger-client/scripts/run_acceptor_ring_4.sh' & 

					ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_3.sh" &
					ssh lucas123@$jupyter "~/shared-logger-client/scripts/run_$app\_$log\_replica_1_ring_4.sh" &
					
					ssh lucas123@$neptune "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_3.sh" &
					ssh lucas123@$neptune "~/shared-logger-client/scripts/run_$app\_$log\_replica_2_ring_4.sh" &
				fi

				sleep 5

				echo "Executando o Experimento [$experiment] App: [$app] Tipo de Log: [$log] # de Threads: [$thread] "
				ssh lucas123@$mars ~/shared-logger-client/scripts/run_experiment.sh $thread $number_of_experiments $thinking_time
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
done 

echo "Fim de execução"


#a=$(ps aux | grep java | grep trunk | cut -d " " -f 5 | awk '{s=$0} NR==1  {printf "tail -f /proc/%s/fd/1",s}');
#echo $a ;
#echo $a | bash

