package org.acme;
/* 
 * Copyright (c) 2013 Università della Svizzera italiana (USI)
 * 
 * This file is part of URingPaxos.
 *
 * URingPaxos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * URingPaxos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with URingPaxos.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.paxos.Util;
import ch.usi.da.smr.Replica;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.Message;

/**
 * Name: Replica<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class ReplicaLoggerClient extends Replica implements LoggerClient {
	private Path path;
	static boolean shouldDeleteStatistic = true;
	static Path latencyStatsPath = Paths.get("/tmp/loggerStatsOutdated");
	int trackerNumber;
	private static AtomicInteger commandsReceivedCounter;
	static Thread stats;
	static Map<Long, Long> allLatencies = new HashMap<>();
	private final static Logger logger = LoggerFactory.getLogger(ReplicaLoggerClient.class);

	public ReplicaLoggerClient(String token, String ringIdRange, int nodeID, int snapshot_modulo, String zoo_host,
			String pathPrefix, int trackerNumber)
			throws Exception {
		super(token, Util.parseRingsArgument(ringIdRange), nodeID, snapshot_modulo, zoo_host);
		this.trackerNumber = trackerNumber;
		path = Paths.get(pathPrefix + "/" + UUID.randomUUID().toString());
		if (shouldDeleteStatistic) {
			shouldDeleteStatistic = false;
			Files.deleteIfExists(latencyStatsPath);
			Files.createFile(latencyStatsPath);
		}

		if (!Files.exists(path))
			Files.createFile(path);
		logger.info("Path created [{}]", path);
		if (commandsReceivedCounter == null)
			commandsReceivedCounter = new AtomicInteger();
		if (stats == null) {
			stats = new Thread("ClientStatsWriter") {
				private int lastReceivedCount = 0;

				@Override
				public void run() {
					while (true) {
						int currentReceivedCount = commandsReceivedCounter.get();

						try {
							System.out.println(
									String.format(
											"%s, %s, %s",
											System.currentTimeMillis(),
											currentReceivedCount - lastReceivedCount, //
											currentReceivedCount));
							lastReceivedCount = currentReceivedCount;
							Thread.sleep(1_000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			};
			stats.start();
		}
	}

	public boolean startThread() {
		return false;
	}

	@Override
	public void receive(Message m) {
		try {
			long currentTimeInNano = System.nanoTime();
			synchronized (path) {
				for (Command command : m.getCommands()) {
					commandsReceivedCounter.incrementAndGet();
					File file = path.toFile();
					FileWriter writer = new FileWriter(file, true);
					writer.write(Arrays.toString(command.getValue()));
					writer.flush();
					writer.close();
				}
				if (commandsReceivedCounter.get() % trackerNumber == 0) {
					long currentLatency = System.nanoTime() - currentTimeInNano;
					allLatencies.put(System.currentTimeMillis(), currentLatency);
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public void close() {
		logger.info("Saving latencies");
		String stringToSave = allLatencies.entrySet().stream()
				.sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).map(
						r -> r.getKey() + "," + r.getValue() + "\n")
				.collect(Collectors.joining());
		try {
			Files.write(latencyStatsPath,
					stringToSave.getBytes(),
					StandardOpenOption.APPEND);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.info("File save");
	}

	@Override
	public List<String> getAllLogs() {
		try {
			if (!Files.exists(path))
				Files.createFile(path);
			logger.debug("Get all logs of client");
			return Files.readAllLines(path).stream().collect(Collectors.toList());
		} catch (IOException e) {
			logger.error("", e);
		}
		return new ArrayList<>();
	}
}
