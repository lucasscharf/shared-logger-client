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
import ch.usi.da.smr.Replica;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.Message;
import java.io.BufferedInputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ch.usi.da.paxos.Util;
import ch.usi.da.paxos.message.Control;
import ch.usi.da.paxos.message.ControlType;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.CommandType;
import ch.usi.da.smr.message.Message;
import ch.usi.da.smr.recovery.DfsRecovery;
import ch.usi.da.smr.recovery.RecoveryInterface;
import ch.usi.da.smr.recovery.SnapshotWriter;
import ch.usi.da.smr.transport.ABListener;
import ch.usi.da.smr.transport.ABSender;
import ch.usi.da.smr.transport.Receiver;
import ch.usi.da.smr.transport.UDPSender;

/**
 * Name: Replica<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class Replica implements Receiver {

	private final static Logger logger = Logger.getLogger(Replica.class);

	public final int nodeID;

	public String token;

	private final PartitionManager partitions;

	private final UDPSender udp;

	private final ABListener ab;

	private final InetAddress ip = Util.getHostAddress();

	private volatile SortedMap<String, byte[]> db;

	private volatile Map<Integer, Long> exec_instance = new HashMap<Integer, Long>();

	private long exec_cmd = 0;

	private int snapshot_modulo = 0; // disabled

	RecoveryInterface stable_storage;

	private volatile boolean recovery = false;

	private volatile boolean active_snapshot = false;
	private boolean embebedLog;
	private Path path;
	private AtomicInteger commandsReceivedCounter;

	public Replica() {
		this.nodeID = 0;
		this.token = "0";
		this.snapshot_modulo = 0;
		partitions = null;
		udp = null;
		ab = null;
		path = Paths.get("/tmp/" + UUID.randomUUID().toString());

		logger.info(String.format(
				"Token [%s], ringId [%s], nodeId [%s], snapshot_modulo [%s], zoo_host [%s], path [%s], embebedLog [%s] with simple constructor",
				token, null, nodeID, snapshot_modulo, null, null, embebedLog));
	}

	public Replica(String token, int[] ringIds, int nodeID, int snapshot_modulo, String zoo_host) throws Exception {
		this(token, ringIds, nodeID, snapshot_modulo, zoo_host, false, "/tmp");
	}

	public Replica(String token, int[] ringIds, int nodeID, int snapshot_modulo, String zoo_host, boolean embebedLog,
			String pathPrefix)
			throws Exception {
		this.nodeID = nodeID;
		this.token = token;
		this.snapshot_modulo = snapshot_modulo;
		this.partitions = new PartitionManager(zoo_host);
		this.embebedLog = embebedLog;
		partitions.init();
		for (int ringId : ringIds) {
			setPartition(partitions.register(nodeID, ringId, ip, token));
		}
		udp = new UDPSender();
		commandsReceivedCounter = new AtomicInteger();

		ab = partitions.getRawABListener(nodeID, ringIds);

		db = new TreeMap<String, byte[]>();
		stable_storage = new DfsRecovery(nodeID, token, "/tmp/smr", partitions);
		path = Paths.get(pathPrefix + "/" + UUID.randomUUID().toString());
		if (!Files.exists(path) && embebedLog)
			Files.createFile(path);

		logger.info(String.format(
				"Token [%s], ringId [%s], nodeId [%s], snapshot_modulo [%s], zoo_host [%s], path [%s], embebedLog [%s]",
				token, ringIds, nodeID, snapshot_modulo, zoo_host, path, embebedLog));

		final Thread stats = new Thread("ClientStatsWriter") {
			private int lastReceivedCount = 0;

			@Override
			public void run() {
				while (true) {
					int currentReceivedCount = commandsReceivedCounter.get();

					try {
						logger.info(
								String.format(
										"Commands received %s, Total Commands %s,",
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

	public void setPartition(Partition partition) {
		logger.info("Replica update partition " + partition);
	}

	public void start() {
		partitions.registerPartitionChangeNotifier(this);
		ab.registerReceiver(this);

		Thread t = new Thread((Runnable) ab);
		t.setName("ABListener");
		t.start();
	}

	public void close() {
		ab.close();
		stable_storage.close();
		partitions.deregister(nodeID, token);
	}

	@Override
	public void receive(Message message) {
		if (message.isSkip() || message.isSetControl()) { // skip skip-instances
			exec_instance.put(message.getRing(), message.getInstnce());
			return;
		}
		List<Command> cmds = new ArrayList<Command>();

		// write snapshot
		exec_cmd++;

		synchronized (db) {
			byte[] data;
			for (Command command : message.getCommands()) {
				try {
					if (embebedLog) {
						String stringToSave = command.toString() + "\n";
						Files.write(path,
								stringToSave.getBytes(),
								StandardOpenOption.APPEND);
					}
				} catch (IOException e) {
					logger.error("", e);
				}
				// TODO implementar lógica de salvar os logs
				switch (command.getType()) {
					case PUT:
						db.put(command.getKey(), command.getValue());
						if (db.containsKey(command.getKey())) {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), "OK".getBytes());
							cmds.add(cmd);
						} else {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), "FAIL".getBytes());
							cmds.add(cmd);
						}
						break;
					case DELETE:
						if (db.remove(command.getKey()) != null) {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), "OK".getBytes());
							cmds.add(cmd);
						} else {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), "FAIL".getBytes());
							cmds.add(cmd);
						}
						break;
					case GET:
						data = db.get(command.getKey());
						if (data != null) {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), data);
							cmds.add(cmd);
						} else {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, command.getKey(), null);
							cmds.add(cmd);
						}
						break;
					default:
						System.err.println("Receive RESPONSE as Command!");
						break;
				}
				commandsReceivedCounter.incrementAndGet();
			}
		}
		exec_instance.put(message.getRing(), message.getInstnce());
		int msg_id = MurmurHash.hash32(message.getInstnce() + "-" + token);
		Message msg = new Message(msg_id, token, message.getFrom(), cmds);
		// logger.debug("Send UDP: " + msg);
		udp.send(msg);
	}

	public Map<Integer, Long> load() {
		try {
			return stable_storage.installState(token, db);
		} catch (Exception e) {
			if (!exec_instance.isEmpty()) {
				return exec_instance;
			} else { // init to 0
				Map<Integer, Long> instances = new HashMap<Integer, Long>();
				instances.put(partitions.getGlobalRing(), 0L);
				for (Partition p : partitions.getPartitions()) {
					instances.put(p.getRing(), 0L);
				}
				return instances;
			}
		}
	}

	public void setActiveSnapshot(boolean b) {
		active_snapshot = b;
	}

	public boolean getRecovery() {
		return recovery;
	}

	/**
	 * Do not accept commands until you know you have recovered!
	 * 
	 * The commands are queued in the learner itself.
	 * 
	 */
	@Override
	public boolean is_ready(Integer ring, Long instance) {
		return true;
	}

	public static boolean newerState(Map<Integer, Long> nstate, Map<Integer, Long> state) {
		for (Entry<Integer, Long> e : state.entrySet()) {
			long i = e.getValue();
			if (i > 0) {
				long ni = nstate.get(e.getKey());
				if (ni > i) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println(
					"Plese use \"Replica\" \"ringID,nodeID,Token\" [snapshot_modulo] [zookeeper host] [embedded log (true|flase)] [path prefix]");
			System.exit(1);
			return;
		}
		startMemoryReplica(args);
	}

	private static void startMemoryReplica(String[] args) {
		String zoo_host = "127.0.0.1:2181";
		int snapshot = 0;

		if (args.length > 2) {
			zoo_host = args[2];
		}
		if (args.length > 1) {
			snapshot = Integer.parseInt(args[1]);
		}
		boolean embebedLog = true;
		if (args.length > 3) {
			embebedLog = Boolean.valueOf(args[3]);
		}

		String pathPrefix = "/tmp";
		if (args.length > 4) {
			pathPrefix = args[4];
		}

		String[] arg = args[0].split(",");
		String ringIdRange = arg[0];
		int[] ringIds;

		if (ringIdRange.contains("-")) {
			String[] range = ringIdRange.split("-");
			int beginRange = Integer.parseInt(range[0]);
			int endRange = Integer.parseInt(range[1]) + 1;
			int delta = endRange - beginRange;
			ringIds = new int[delta];
			for (int i = beginRange; i < endRange; i++) {
				ringIds[i] = beginRange + i;
			}
		} else {
			int ringId = Integer.parseInt(ringIdRange);
			ringIds = new int[1];
			ringIds[0] = ringId;
		}

		int nodeId = Integer.parseInt(arg[1]);

		final String token = arg[2];

		try {
			final Replica replica = new Replica(token, ringIds, nodeId, snapshot, zoo_host, embebedLog, pathPrefix);
			Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
				@Override
				public void run() {
					replica.close();
				}
			});
			replica.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			in.readLine();
			replica.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
