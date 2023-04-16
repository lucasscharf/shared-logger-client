#!/bin/bash

number_of_threads=$1

cd ~/shared-logger-client/scripts
./execute_experiment.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_1 &
./execute_experiment_ring_2.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_2 
# ./execute_experiment_ring_3.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_3 &
# ./execute_experiment_ring_4.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_4 
echo "Ending experiments"
killall -9 java

exit
