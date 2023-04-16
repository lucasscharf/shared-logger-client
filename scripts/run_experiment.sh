#!/bin/bash

number_of_threads=$1
number_of_experiments=$2

cd ~/shared-logger-client/scripts
./execute_experiment.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_1 &

if[[ "$number_of_experiments" -eq "2" ]] do
	./execute_experiment_ring_2.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_2 
fi

if[[ "$number_of_experiments" -eq "4" ]] do
	./execute_experiment_ring_2.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_2 &
	./execute_experiment_ring_3.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_3 &
	./execute_experiment_ring_4.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_4 
fi

echo "Ending experiments"
killall -9 java

exit
