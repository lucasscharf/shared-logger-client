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
	private static final String DISK_REPLICA = "diskReplica";
	static {
		// get hostname and pid for log file name
		String host = "localhost";
		try {
			Process proc = Runtime.getRuntime().exec("hostname");
			BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
			proc.waitFor();
			byte[] b = new byte[in.available()];
			in.read(b);
			in.close();
			host = new String(b).replace("\n", "");
		} catch (IOException | InterruptedException e) {
		}
		int pid = 0;
		try {
			pid = Integer.parseInt((new File("/proc/self")).getCanonicalFile().getName());
		} catch (NumberFormatException | IOException e) {
		}
		System.setProperty("logfilename", "L" + host + "-" + pid + ".log");
	}

	private final static Logger logger = Logger.getLogger(Replica.class);

	public final int nodeID;

	public String token;

	private final PartitionManager partitions;

	private volatile int min_token;

	private volatile int max_token; // if min_token > max_token : replica serves whole key space

	private final UDPSender udp;

	private final ABListener ab;

	private final InetAddress ip = Util.getHostAddress();

	private volatile SortedMap<String, byte[]> db;

	private volatile Map<Integer, Long> exec_instance = new HashMap<Integer, Long>();

	private long exec_cmd = 0;

	private int snapshot_modulo = 0; // disabled

	private final boolean use_thrift = false;

	RecoveryInterface stable_storage;

	private volatile boolean recovery = false;

	private volatile boolean active_snapshot = false;
	private boolean embebedLog = true;
	private Path path;

	public Replica() {
		this.nodeID = 0;
		this.token = "0";
		this.snapshot_modulo = 0;
		partitions = null;
		udp = null;
		ab = null;
		path = Paths.get("/tmp/" + UUID.randomUUID().toString());
		if (!Files.exists(path))
			Files.createFile(path);
		System.out.println(String.format(
				"Token [%s], ringId [%s], nodeId [%s], snapshot_modulo [%s], zoo_host [%s], path [%s], embebedLog [%s]",
				token, null, nodeID, snapshot_modulo, null, path, embebedLog));
	}

	public Replica(String token, int ringID, int nodeID, int snapshot_modulo, String zoo_host) throws Exception {
		this(token, ringID, nodeID, snapshot_modulo, zoo_host, false);
	}

	public Replica(String token, int ringID, int nodeID, int snapshot_modulo, String zoo_host, boolean embebedLog)
			throws Exception {
		this.nodeID = nodeID;
		this.token = token;
		this.snapshot_modulo = snapshot_modulo;
		this.partitions = new PartitionManager(zoo_host);
		partitions.init();
		setPartition(partitions.register(nodeID, ringID, ip, token));
		udp = new UDPSender();
		if (use_thrift) {
			ab = partitions.getThriftABListener(ringID, nodeID);
		} else {
			ab = partitions.getRawABListener(ringID, nodeID);
		}
		db = new TreeMap<String, byte[]>();
		// stable_storage = new HttpRecovery(partitions);
		stable_storage = new DfsRecovery(nodeID, token, "/tmp/smr", partitions);
		path = Paths.get("/tmp/" + UUID.randomUUID().toString());
		if (!Files.exists(path))
			Files.createFile(path);

		System.out.println(String.format(
				"Token [%s], ringId [%s], nodeId [%s], snapshot_modulo [%s], zoo_host [%s], path [%s], embebedLog [%s]",
				token, null, nodeID, snapshot_modulo, null, path, embebedLog));
	}

	public void setPartition(Partition partition) {
		logger.info("Replica update partition " + partition);
		min_token = partition.getLow();
		max_token = partition.getHigh();
	}

	public void start() {
		partitions.registerPartitionChangeNotifier(this);
		// install old state
		// FIXME: disabled recovery! exec_instance = load();
		// start listening
		ab.registerReceiver(this);
		if (min_token > max_token) {
			logger.info("Replica start serving partition " + token + ": whole key space");
		} else {
			logger.info("Replica start serving partition " + token + ": " + min_token + "->" + max_token);
		}
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
		if (snapshot_modulo > 0 && exec_cmd % snapshot_modulo == 0) {
			async_checkpoint();
		}

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
					case GETRANGE:
						String start_key = command.getKey();
						String end_key = new String(command.getValue()).split(";")[0];
						int count = command.getCount();
						logger.info("getrange " + start_key + " -> " + end_key + " (" + MurmurHash.hash32(start_key) + "->"
								+ MurmurHash.hash32(end_key) + ")");
						int msg = 0;
						int msg_size = 0;
						for (Entry<String, byte[]> e : db.tailMap(start_key).entrySet()) {
							if (msg >= count || (!end_key.isEmpty() && e.getKey().compareTo(end_key) > 0)) {
								break;
							}
							if (msg_size >= 50000) {
								break;
							} // send by UDP
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, e.getKey(), e.getValue());
							msg_size += e.getValue().length;
							cmds.add(cmd);
							msg++;
						}
						if (msg == 0) {
							Command cmd = new Command(command.getID(), CommandType.RESPONSE, "", null);
							cmds.add(cmd);
						}
						// signal
						partitions.singal(token, command);
						// wait until signal from every involved partition
						boolean ret = partitions.waitSignal(command);
						if (ret != true) {
							cmds.clear();
						}
						break;
					default:
						System.err.println("Receive RESPONSE as Command!");
						break;
				}
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

	public boolean sync_checkpoint() {
		if (stable_storage.storeState(exec_instance, db)) {
			try {
				for (Entry<Integer, Long> e : exec_instance.entrySet()) {
					ab.safe(e.getKey(), e.getValue());
				}
				logger.info("Replica checkpointed up to instance " + exec_instance);
				return true;
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return false;
	}

	public boolean async_checkpoint() {
		if (!active_snapshot) {
			active_snapshot = true;
			// shallow copy
			Map<Integer, Long> old_exec_instance = new HashMap<Integer, Long>(exec_instance);
			SortedMap<String, byte[]> old_db = new TreeMap<String, byte[]>(db);
			// deep copy
			/*
			 * Map<Integer,Long> old_exec_instance = new HashMap<Integer,Long>();
			 * for(Entry<Integer,Long> e : exec_instance.entrySet()){
			 * old_exec_instance.put(new Integer(e.getKey()),new Long(e.getValue()));
			 * }
			 * SortedMap<String,byte[]> old_db = new TreeMap<String,byte[]>();
			 * for(Entry<String,byte[]> e : db.entrySet()){
			 * old_db.put(new
			 * String(e.getKey()),Arrays.copyOf(e.getValue(),e.getValue().length));
			 * }
			 * old_db.putAll(db);
			 */
			Thread t = new Thread(new SnapshotWriter(this, old_exec_instance, old_db, stable_storage, ab));
			t.start();
		} else {
			logger.info("Async checkpoint supressed since other active!");
		}
		return true;
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
		if (instance <= exec_instance.get(ring) + 1) {
			if (recovery == true) {
				recovery = false;
				logger.info("Recovery set false.");
			}
			return true;
		}
		if (recovery == false) {
			recovery = true;
			Thread t = new Thread() {
				@Override
				public void run() {
					logger.info("Replica starts recovery thread.");
					while (getRecovery()) {
						exec_instance = load();
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
					logger.info("Recovery thread stopped.");
				}
			};
			t.setName("Recovery");
			t.start();
		}
		return false;
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
			System.err.println("Plese use \"Replica\" \"ringID,nodeID,Token\" [snapshot_modulo] [zookeeper host] [embedded log (true|flase)]");
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
		if(args.length > 3) {
			embebedLog = Boolean.valueOf(args[3]);
		}


		String[] arg = args[0].split(",");
		final int nodeID = Integer.parseInt(arg[1]);
		final int ringID = Integer.parseInt(arg[0]);
		final String token = arg[2];

		try {
			final Replica replica = new Replica(token, ringID, nodeID, snapshot, zoo_host, embebedLog);
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
