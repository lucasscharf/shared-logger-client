#!/bin/bash

killall -9 java

cd ~/shared-logger-client/URingPaxos/target/Paxos-trunk ; ./thriftnode.sh 1 1:PA 10.10.1.1