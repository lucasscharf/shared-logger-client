package ch.usi.da.smr;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import ch.usi.da.paxos.Util;
import ch.usi.da.paxos.message.Control;
import ch.usi.da.paxos.message.ControlType;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.CommandType;
import ch.usi.da.smr.message.Message;
import ch.usi.da.smr.transport.Receiver;
import ch.usi.da.smr.transport.Response;
import ch.usi.da.smr.transport.UDPListener;

/**
 * Name: Client<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class Client implements Receiver {
	private final static Logger logger = Logger.getLogger(Client.class);

	private final PartitionManager partitions;

	private final long thinkingTime;
	private final int writePercentage;
	private final int trackerNumber;
	private final AtomicInteger commandsSendCounter;
	private final AtomicInteger responsesReceivedCounter;
	private final ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<>();

	private final UDPListener udp;

	private Map<Integer, Response> commands = new ConcurrentHashMap<Integer, Response>();

	private Map<Integer, List<Command>> responses = new ConcurrentHashMap<Integer, List<Command>>();

	private Map<Integer, List<String>> await_response = new ConcurrentHashMap<Integer, List<String>>();

	private Map<Integer, BlockingQueue<Response>> send_queues = new HashMap<Integer, BlockingQueue<Response>>();

	// we need only one response per replica group
	Set<Long> delivered = Collections.newSetFromMap(new LinkedHashMap<Long, Boolean>() {
		private static final long serialVersionUID = -5674181661800265432L;

		protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
			return size() > 50000;
		}
	});

	private final InetAddress ip;

	private final int port;

	private final Map<Integer, Integer> connectMap;

	public Client(PartitionManager partitions, Map<Integer, Integer> connectMap, long thinkingTime, int writePercentage,
			int trackerNumber)
			throws IOException {
		this.partitions = partitions;
		this.connectMap = connectMap;
		this.thinkingTime = thinkingTime;
		this.writePercentage = writePercentage;
		this.trackerNumber = trackerNumber;
		commandsSendCounter = new AtomicInteger();
		responsesReceivedCounter = new AtomicInteger();

		ip = Util.getHostAddress();
		port = 5000 + new Random().nextInt(15000);
		udp = new UDPListener(port);
		Thread t = new Thread(udp);
		t.setName("UDPListener");
		t.start();
	}

	public void init() {
		udp.registerReceiver(this);
	}

	public void readStdin() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String commandLine;
		try {
			int id = 0;
			Command cmd = null;
			while ((commandLine = in.readLine()) != null && commandLine.length() != 0) {
				// read input
				String[] line = commandLine.split("\\s+");
				if (commandLine.startsWith("start")) {
					cmd = null;
					id = handleAutomaticTest(commandLine);
				} else if (line.length > 3) {
					cmd = handleFourParametersCommand(id, cmd, line);
				} else if (line.length > 2) {
					cmd = handleThreeParametersCommand(id, cmd, line);
				} else if (line.length > 1) {
					cmd = handleTwoParametersCommand(id, cmd, line);
				} else {
					System.out.println("Add command: <PUT|GET|GETRANGE|DELETE> key <value>");
				}

				id = sendCommandIfNecessary(id, cmd);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}
		stop();
	}

	private int handleAutomaticTest(String commandLine) throws InterruptedException {
		int id;
		final int numberOfThreads; // # of threads
		final int sendsPerThread;
		final int commandSize;
		final int key_count = 50000; // 50k * 1k byte memory needed at replica
		String[] sl = commandLine.split(" ");
		if (sl.length > 1) {
			numberOfThreads = Integer.parseInt(sl[1]);
			sendsPerThread = Integer.parseInt(sl[2]);
			commandSize = Integer.parseInt(sl[3]);
		} else {
			numberOfThreads = 70;
			sendsPerThread = 15000;
			commandSize = 1024;
		}
		final AtomicInteger send_id = new AtomicInteger(0);

		final CountDownLatch await = new CountDownLatch(numberOfThreads);
		final Thread stats = new Thread("ClientStatsWriter") {
			private int lastSentCount = 0;
			private int lastReceivedCount = 0;

			@Override
			public void run() {
				while (await.getCount() > 0) {
					int currentSentCount = commandsSendCounter.get();
					int currentReceiverCount = responsesReceivedCounter.get();

					try {
						logger.info(
								String.format(
										"Commands sent (since last call) %s. Response received (since last call) %s. Total Commands %s. Total Responses %s. Avg latency %.1f ms",
										currentSentCount - lastSentCount, //
										currentReceiverCount - lastReceivedCount, //
										currentSentCount, //
										currentReceiverCount,//
										latencies.stream().mapToLong(m -> m).average().orElse(0.0) / 1_000_000
										));
						latencies.clear();
						Thread.sleep(1_000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		};
		stats.start();
		logger.info("Start performance testing with [" + numberOfThreads + "] threads.");
		logger.info("(sendsPerThread:" + sendsPerThread + " value_size:" + commandSize + " bytes)");
		for (int i = 0; i < numberOfThreads; i++) {
			Thread t = new Thread("Command Sender " + i) {
				@Override
				public void run() {
					logger.info("Command sender initialized");
					int sendCount = 0;
					while (sendCount < sendsPerThread) {
						int id = send_id.incrementAndGet();
						Command cmd = null;

						int randomChance = ((int) (Math.random() * 100.0));

						if (randomChance < writePercentage) {
							cmd = new Command(id, CommandType.PUT, "user" + (id % key_count), new byte[commandSize]);
						} else {
							int targetId = ((int) (Math.random() * (double) key_count) % id);
							cmd = new Command(id, CommandType.GET, "user" + targetId, new byte[0]);
						}
						Response response = null;
						try {
							long currentTimeInNano = System.nanoTime();
							commandsSendCounter.incrementAndGet();
							if ((response = send(cmd)) != null) {
								response.getResponse(1000); // wait response
								int currentResponse = responsesReceivedCounter.incrementAndGet();
								if (currentResponse % trackerNumber == 0) {
									long currentLatency = System.nanoTime() - currentTimeInNano;
									latencies.add(currentLatency);
									// logger.info(String.format(
									// "Recebi a resposta para o comando [%s] com latncia [%s] (delete-me depois)",
									// cmd,
									// currentLatency));
								}
							}
						} catch (Exception e) {
							logger.error("Error in send thread!", e);
						}
						sendCount++;
						try {
							Thread.sleep(thinkingTime);
						} catch (Exception ex) {
							logger.error("we have a problem", ex);
						}
					}
					await.countDown();
					logger.debug("Thread [" + Thread.currentThread().getId() + "] terminated.");
				}
			};
			t.start();
		}
		await.await(); // wait until finished
		id = send_id.incrementAndGet();
		Thread.sleep(5000);
		return id;
	}

	private int sendCommandIfNecessary(int id, Command cmd) throws Exception {
		// send a command
		if (cmd != null) {
			Response r = null;
			if ((r = send(cmd)) != null) {
				List<Command> response = r.getResponse(10000); // wait response
				if (!response.isEmpty()) {
					for (Command c : response) {
						if (c.getType() == CommandType.RESPONSE) {
							if (c.getValue() != null) {
								System.out.println("  -> " + new String(c.getValue()));
							} else {
								System.out.println("<no entry>");
							}
						}
					}
					id++; // re-use same ID if you run into a timeout
				} else {
					System.err.println("Did not receive response from replicas: " + cmd);
				}
			} else {
				System.err.println("Could not send command: " + cmd);
			}
		}
		return id;
	}

	private Command handleTwoParametersCommand(int id, Command cmd, String[] line) {
		try {
			cmd = new Command(id, CommandType.valueOf(line[0].toUpperCase()), line[1], new byte[0]);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}
		return cmd;
	}

	private Command handleThreeParametersCommand(int id, Command cmd, String[] line) {
		try {
			cmd = new Command(id, CommandType.valueOf(line[0].toUpperCase()), line[1], line[2].getBytes());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}
		return cmd;
	}

	private Command handleFourParametersCommand(int id, Command cmd, String[] line) {
		try {
			String arg2 = line[2];
			if (arg2.equals(".")) {
				arg2 = "";
			} // simulate empty string
			cmd = new Command(id, CommandType.valueOf(line[0].toUpperCase()), line[1], arg2.getBytes(),
					Integer.parseInt(line[3]));
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}
		return cmd;
	}

	public void stop() {
		udp.close();
	}

	public void prepareGlobal(int cmdID, int groupID) throws Exception {
		int oldRing = groupID;
		int newRing = partitions.getGlobalRing();
		Control c = new Control(cmdID, ControlType.Prepare, groupID, newRing);
		Response r1 = new Response(c);
		commands.put(c.getID(), r1);
		Message m = new Message(1, ip.getHostAddress() + ";" + port, "", null);
		m.setControl(r1.getControl());
		partitions.sendRing(oldRing, m);
		Response r2 = new Response(c);
		commands.put(c.getID(), r2);
		Message m2 = new Message(1, ip.getHostAddress() + ";" + port, "", null);
		m2.setControl(r2.getControl());
		partitions.sendRing(newRing, m2);
	}

	public void subscribeGlobal(int cmdID, int groupID) throws Exception {
		int oldRing = groupID;
		int newRing = partitions.getGlobalRing();
		Control c = new Control(cmdID, ControlType.Subscribe, groupID, newRing);
		Response r1 = new Response(c);
		commands.put(c.getID(), r1);
		Message m = new Message(1, ip.getHostAddress() + ";" + port, "", null);
		m.setControl(r1.getControl());
		partitions.sendRing(oldRing, m);
		Response r2 = new Response(c);
		commands.put(c.getID(), r2);
		Message m2 = new Message(1, ip.getHostAddress() + ";" + port, "", null);
		m2.setControl(r2.getControl());
		partitions.sendRing(newRing, m2);
	}

	public void unsubscribeGlobal(int cmdID, int groupID) throws Exception {
		int removeRing = partitions.getGlobalRing();
		Control c = new Control(cmdID, ControlType.Unsubscribe, groupID, removeRing);
		Response r1 = new Response(c);
		commands.put(c.getID(), r1);
		Message m = new Message(1, ip.getHostAddress() + ";" + port, "", null);
		m.setControl(r1.getControl());
		partitions.sendRing(removeRing, m);
	}

	/**
	 * Send a command (use same ID if your Response ended in a timeout)
	 * 
	 * (the commands will be batched to larger Paxos instances)
	 * 
	 * @param cmd The command to send
	 * @return A Response object on which you can wait
	 * @throws Exception
	 */
	public Response send(Command cmd) throws Exception {
		Response r = new Response(cmd);
		commands.put(cmd.getID(), r);
		int partition = -1;
		if (cmd.getType() == CommandType.GETRANGE) {
			List<String> await = new ArrayList<String>();
			for (Partition p : partitions.getPartitions()) {
				await.add(p.getID());
			}
			// TODO: also subset of partition await_response.put(cmd.getID(),await);
		} else {
			partition = partitions.getPartition(cmd.getKey());
			// special case for EC2 inter-region app;
			String single_part = System.getenv("PART");
			if (single_part != null) {
				partition = Integer.parseInt(single_part);
			}
		}
		synchronized (send_queues) {
			if (!send_queues.containsKey(partition)) {
				send_queues.put(partition, new LinkedBlockingQueue<Response>());
				Thread t = new Thread(new BatchSender(partition, this));
				t.setName("BatchSender-" + partition);
				t.start();
			}
		}
		send_queues.get(partition).add(r);
		return r;
	}

	@Override
	public synchronized void receive(Message m) {
		logger.debug("Client received ring " + m.getRing() + " instnace " + m.getInstnce() + " (" + m + ")");

		// filter away already received replica answers
		long hash = MurmurHash.hash64(m.getID() + "-" + m.getInstnce());
		if (delivered.contains(hash)) {
			return;
		} else {
			delivered.add(hash);
		}

		// un-batch response (multiple responses per command_id)
		for (Command c : m.getCommands()) {
			if (!responses.containsKey(c.getID())) {
				List<Command> l = new ArrayList<Command>();
				responses.put(c.getID(), l);
			}
			List<Command> l = responses.get(c.getID());
			if (!c.getKey().isEmpty() && !l.contains(c)) {
				l.add(c);
			}
		}

		// set responses to open commands
		Iterator<Entry<Integer, List<Command>>> it = responses.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, List<Command>> e = it.next();
			if (commands.containsKey(e.getKey())) {
				if (await_response.containsKey(e.getKey())) { // handle GETRANGE responses from different partitions
					await_response.get(e.getKey()).remove(m.getFrom());
					if (await_response.get(e.getKey()).isEmpty()) {
						commands.get(e.getKey()).setResponse(e.getValue());
						commands.remove(e.getKey());
						await_response.remove(e.getKey());
						it.remove();
					}
				} else {
					commands.get(e.getKey()).setResponse(e.getValue());
					commands.remove(e.getKey());
					it.remove();
				}
			} else {
				it.remove();
			}
		}
	}

	public PartitionManager getPartitions() {
		return partitions;
	}

	public Map<Integer, BlockingQueue<Response>> getSendQueues() {
		return send_queues;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public Map<Integer, Integer> getConnectMap() {
		return connectMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String zoo_host = "127.0.0.1:2181";
		if (args.length > 1) {
			zoo_host = args[1];
		}
		long thinkingTime = 100;
		if (args.length > 2) {
			thinkingTime = Long.parseLong(args[2]);
		}

		int writePercentage = 100;
		if (args.length > 3) {
			writePercentage = Integer.parseInt(args[3]);
		}

		int trackerNumber = 100;
		if (args.length > 4) {
			trackerNumber = Integer.parseInt(args[4]);
		}

		if (args.length < 1) {
			System.err.println(
					"Plese use \"Client\" \"ring ID,node ID[;ring ID,node ID] [thinkingTime] [write percentage [0;100] [command tracker number]] \"");
		} else {
			final Map<Integer, Integer> connectMap = parseConnectMap(args[0]);
			try {
				final PartitionManager partitions = new PartitionManager(zoo_host, connectMap);
				partitions.init();
				final Client client = new Client(partitions, connectMap, thinkingTime, writePercentage, trackerNumber);
				Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
					@Override
					public void run() {
						client.stop();
					}
				});
				client.init();
				client.readStdin();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static Map<Integer, Integer> parseConnectMap(String arg) {
		Map<Integer, Integer> connectMap = new HashMap<Integer, Integer>();
		for (String s : arg.split(";")) {
			connectMap.put(Integer.valueOf(s.split(",")[0]), Integer.valueOf(s.split(",")[1]));
		}
		return connectMap;
	}

	@Override
	public boolean is_ready(Integer ring, Long instance) {
		return true;
	}
}
