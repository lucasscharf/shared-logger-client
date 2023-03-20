#!/bin/bash

ipZookeeper=pc840.emulab.net
ipProposer=pc769.emulab.net
ipAcceptor=pc858.emulab.net
ipReplica1=pc772.emulab.net
ipReplica2=pc751.emulab.net
ipReplica3=pc710.emulab.net
ipProposer_ring_2=pc706.emulab.net
ipAcceptor_ring_2=pc776.emulab.net
ipReplica1_ring_2=pc855.emulab.net
ipReplica2_ring_2=pc780.emulab.net
ipReplica3_ring_2=pc749.emulab.net
ipClient=pc752.emulab.net
ipLogger1=pc854.emulab.net
ipLogger2=pc762.emulab.net

apps=(io)
logs=(dec)
threads=(8 16 32 64 128 256 512 1024 2048)

ips=($ipReplica1 $ipLogger1 $ipProposer $ipAcceptor $ipLogger2 $ipReplica2 $ipReplica3 $ipReplica1 $ipLogger1_ring_2 $ipProposer_ring_2 $ipAcceptor_ring_2 $ipReplica1_ring_2 $ipReplica2_ring_2 $ipReplica3_ring_2 $ipClient)


cd ~/shared-logger-client/server/
mvn clean install -DskipTests 

cd ~/shared-logger-client/scripts 

for ip in "${ips[@]}" 
do
	ssh lucas123@$ip 'sudo chown lucas123 /media ; sudo chmod 777 /media; '
done 

for ip in "${ips[@]}" 
do 
	ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
done 

# for app in "${apps[@]}" 
# do
# 	for log in "${logs[@]}"
# 	do
# 		for thread in "${threads[@]}"
# 		do
# 			outputFile=~/shared-logger-client/evaluation/thinking_time_50/1ring/$app\_$log\_$thread\_90_001/
# 			echo "~/shared-logger-client/scripts/run_quarkus_stressTest.sh $thread 1"
# 			ssh lucas123@$ipLogger2 "~/shared-logger-client/scripts/run_quarkus_stressTest.sh $thread 1"

# 			echo "Killing sshs"
# 			killall -9 ssh


# 			for ip in "${ips[@]}" 
# 			do 
# 				ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
# 			done 

# 			echo "Getting logs"
# 			~/shared-logger-client/scripts/pegarLogs.sh $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile $ipReplica1_ring_2 $ipReplica2_ring_2 $ipReplica3_ring_2

# 			echo "Cleaning tmps"
# 			for ip in "${ips[@]}" 
# 			do 
# 				echo "Cleaning ip: $ip"
# 				ssh lucas123@$ip ' sudo rm -rf /media/disk1/* ; sudo rm -rf /media/disk2/*'
# 			done 

# 			echo "Rebuilding zookeeper"
# 			~/shared-logger-client/scripts/clean_zookeeper.sh > /dev/null
# 			killall -9 ssh
# 		done
# 	done
# done

for app in "${apps[@]}" 
do
	for log in "${logs[@]}"
	do
		for thread in "${threads[@]}"
		do
			outputFile=~/shared-logger-client/evaluation/thinking_time_50/2rings/$app\_$log\_$thread\_90_001/

			ssh lucas123@$ipLogger2 "~/shared-logger-client/scripts/run_quarkus_stressTest.sh $thread 2"

			echo "Killing sshs"
			killall -9 ssh


			for ip in "${ips[@]}" 
			do 
				ssh lucas123@$ip '~/shared-logger-client/scripts/kill_all_java.sh'
			done 

			echo "Getting logs"
			~/shared-logger-client/scripts/pegarLogs.sh $ipLogger1 $ipLogger2 $ipReplica1 $ipReplica2 $ipReplica3 $ipClient $outputFile $ipReplica1_ring_2 $ipReplica2_ring_2 $ipReplica3_ring_2

			echo "Rebuilding zookeeper"
			~/shared-logger-client/scripts/clean_zookeeper.sh > /dev/null
			killall -9 ssh
		done
	done
done

echo "Fim de execução"


