#!/bin/bash
max_threads_per_node=$1
requests_per_thread=$2
thread_size=$3
ip=$4

printf "start ${max_threads_per_node} ${requests_per_thread} ${thread_size} \n" | ~/shared-logger-client/URingPaxos/clients/SMR/target/SMR-trunk/client.sh 1,1 ${ip}
