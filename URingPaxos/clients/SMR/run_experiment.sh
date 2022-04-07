#!/bin/bash
max_threads_per_node=$1
requests_per_thread=$2
thread_size=$3

# printf "start ${max_threads_per_node} ${requests_per_thread} ${thread_size}\n" 
printf "start ${max_threads_per_node} ${requests_per_thread} ${thread_size}\n" | target/SMR-trunk/client.sh 1,1
