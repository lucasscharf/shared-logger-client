package org.acme;

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

public class LoadGenerator implements Runnable {
	private ReplicaLoggerClient replicaLoggerClient;
	private static final ConcurrentHashMap<Long, Long> allLatencies = new ConcurrentHashMap<>();
	private Integer experimentTimeSeconds = 30;
	private AtomicInteger responsesReceivedCounter;
	private Integer trackerNumber = 1000;
	private static boolean shouldDeleteStatistic = true;
	private static Path latencyStatsPath = Paths.get("/tmp/loggerStats");

	public LoadGenerator(ReplicaLoggerClient replicaLoggerClient) {
		this.replicaLoggerClient = replicaLoggerClient;
		allLatencies.put(0L, 0L);

		if (shouldDeleteStatistic) {
			try {
				shouldDeleteStatistic = false;
				Files.deleteIfExists(latencyStatsPath);
				Files.createFile(latencyStatsPath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		long initialTimeSeconds = System.currentTimeMillis() / 1000;
		long currentTimeSeconds = System.currentTimeMillis() / 1000;

		allLatencies.put(System.currentTimeMillis(), 0L);

		while ((initialTimeSeconds + experimentTimeSeconds) > currentTimeSeconds) {
			currentTimeSeconds = System.currentTimeMillis() / 1000;
			long currentTimeInNano = System.nanoTime();

			replicaLoggerClient.receive(buildMessage());

			int currentResponse = responsesReceivedCounter.incrementAndGet();
			if (currentResponse % trackerNumber == 0) {
				long currentLatency = System.nanoTime() - currentTimeInNano;
				allLatencies.put(System.currentTimeMillis(), currentLatency);
			}
			sleepForThinkingTime();
		}
	}

	public static void savingLatencies() {

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
	}

	private Message buildMessage() {
		Command cmd = new Command(responsesReceivedCounter.get(), CommandType.PUT,
				"user" + UUID.randomUUID().toString() + "-" + Thread.currentThread().getName(),
				new byte[1024]);

		Message m = new Message(0, "from", "to", Arrays.asList(cmd));
		return m;
	}

	private void sleepForThinkingTime() {
		try {
			long thinkingTime = 55;
			long timeToSleep = ((long) (Math.random() * 10000.0)) % thinkingTime;
			Thread.sleep(timeToSleep);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
