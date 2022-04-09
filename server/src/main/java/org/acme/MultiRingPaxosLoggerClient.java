package org.acme;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.paxos.Util;
import ch.usi.da.paxos.api.PaxosRole;
import ch.usi.da.paxos.ring.RingDescription;
import ch.usi.da.smr.MurmurHash;
import ch.usi.da.smr.Partition;
import ch.usi.da.smr.PartitionManager;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.Message;
import ch.usi.da.smr.transport.ABListener;
import ch.usi.da.smr.transport.RawABListener;
import ch.usi.da.smr.transport.Receiver;
import ch.usi.da.smr.transport.UDPSender;

public class MultiRingPaxosLoggerClient implements LoggerClient, Receiver {
	private final ABListener ab;
	private static final String TOKEN = "1";
	private Integer nodeId;
	private PartitionManager partitions;
	private final InetAddress ip = Util.getHostAddress();
	private final UDPSender udp;
	private List<String> logs;

	private final static Logger logger = LoggerFactory.getLogger(MultiRingPaxosLoggerClient.class);

	public MultiRingPaxosLoggerClient(String zookeeperUrl, Integer nodeId, Integer ringId) {
		logs = new LinkedList<>();
		this.nodeId = nodeId;
		partitions = new PartitionManager(zookeeperUrl);
		try {
			partitions.init();
			Partition partition = partitions.register(nodeId, ringId, ip, TOKEN);
			logger.info("Partition [{}]", partition);
			udp = new UDPSender();

			List<PaxosRole> role = new ArrayList<>();
			role.add(PaxosRole.Learner);
			List<RingDescription> rings = new ArrayList<>();
			rings.add(new RingDescription(ringId, role));

			logger.debug("Create RawABListener " + rings);
			Thread.sleep(1000); // wait until PartitionManger is ready

			ab = new RawABListener(nodeId, zookeeperUrl, rings);
			ab.registerReceiver(this);

			Thread t = new Thread((Runnable) ab);
			t.setName("ABListener");
			t.start();
		} catch (KeeperException | InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}

		logger.info("Registration completed");
	}

	@Override
	public List<String> getAllLogs() {
		return logs;
	}

	@Override
	public void receive(Message message) {
		logger.info("Logger received ring [{}] from instance [{}] and message [{}]", message.getRing(),
				message.getInstnce(), message);

		if (message.isSkip() || message.isSetControl()) { // skip skip-instances
			logger.debug("Control msg. Skipped");
			return;
		}
		message.getCommands().stream().map(Command::toString).forEach(logs::add);
		logger.info("Log size [{}]", logs.size());
		logger.info("send udp message");

		int msg_id = MurmurHash.hash32(message.getInstnce() + "-" + TOKEN);
		Message msg = new Message(msg_id, TOKEN, message.getFrom(), message.getCommands());
		udp.send(msg);
	}

	@Override
	public boolean is_ready(Integer ring, Long instance) {
		return true;
	}

	@Override
	public void close() {
		ab.close();
		partitions.deregister(nodeId, TOKEN);
	}
}
