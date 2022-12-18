#!/bin/bash
max_threads_per_node=$1
requests_per_thread=$2
thread_size=$3
ip=$4
thinkingTime=$5
writePercentage=$6
commandTrackerNumber=$7

printf "start ${max_threads_per_node} ${requests_per_thread} ${thread_size} \n" | ~/shared-logger-client/URingPaxos/clients/SMR/target/SMR-trunk/client.sh 2,21 ${ip} ${thinkingTime} ${writePercentage} 