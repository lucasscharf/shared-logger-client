package org.acme;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.paxos.Util;
import ch.usi.da.paxos.message.Control;
import ch.usi.da.paxos.message.ControlType;
import ch.usi.da.smr.MurmurHash;
import ch.usi.da.smr.PartitionManager;
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

public class MultRingPaxosLoggerClient implements LoggerClient, Receiver {
	private final ABListener ab;
	private static final String TOKEN = "1";
	private Integer nodeId;
	private Integer ringId;
	private String zookeeperUrl;
	private PartitionManager partitions;
	private final InetAddress ip = Util.getHostAddress();
	private final UDPSender udp;

	private List<String> logs;

	private final static Logger logger = LoggerFactory.getLogger(MultRingPaxosLoggerClient.class);

	public MultRingPaxosLoggerClient(String zookeeperUrl, Integer nodeId, Integer ringId) {
		logs = new LinkedList<>();
		this.zookeeperUrl = zookeeperUrl;
		this.nodeId = nodeId;
		this.ringId = ringId;
		partitions = new PartitionManager(zookeeperUrl);
		try {
			partitions.init();
			partitions.register(nodeId, ringId, ip, TOKEN);
			ab = partitions.getRawABListener(ringId, nodeId);
			udp = new UDPSender();

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
