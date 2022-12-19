#!/bin/bash

number_of_threads=$1

cd ~/shared-logger-client/scripts
./execute_experiment.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao &
./execute_experiment_ring_2.sh $number_of_threads 30 1024 10.10.1.1 50 100 1000 > /tmp/execucao_2 