package org.acme;

import java.util.concurrent.SynchronousQueue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.CommandType;
import ch.usi.da.smr.message.Message;

public class ConsumerGenerator implements Runnable {
	SynchronousQueue<Message> queue;
	ReplicaLoggerClient replicaLoggerClient;

	public ConsumerGenerator(ReplicaLoggerClient replicaLoggerClient, SynchronousQueue<Message> queue) {
		this.replicaLoggerClient = replicaLoggerClient;
		this.queue = queue;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Message message = queue.take();
				replicaLoggerClient.receive(message);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
