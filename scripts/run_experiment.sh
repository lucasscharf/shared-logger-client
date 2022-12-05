#!/bin/bash

number_of_threads=$1

cd ~/shared-logger-client/scripts
./execute_experiment.sh $number_of_threads 90 1024 10.10.1.1 50 > /tmp/execucao 
