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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.apache.zookeeper.KeeperException;

import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.Message;
import ch.usi.da.smr.transport.Response;
import ch.usi.da.smr.transport.ABSender;
import ch.usi.da.smr.transport.ThriftABSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.apache.zookeeper.KeeperException;

/**
 * Name: BatchSender<br>
 * Description: <br>
 * 
 * Creation date: Nov 29, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class BatchSender implements Runnable {

	// private final static Logger logger = Logger.getLogger(BatchSender.class);

	// //private final ABSender sender;

	// private final Client client;

	// private final BlockingQueue<Response> queue;

	// private final int batch_size = 8912; // 0: disable

	// private final int partition;

	// public BatchSender(int partition, Client client) throws TTransportException,
	// IOException, KeeperException, InterruptedException {
	// this.client = client;
	// this.partition = partition;
	// queue = client.getSendQueues().get(partition);
	// }

	// @Override
	// public void run() {
	// 	logger.info("Sending batch");
	// 	while (true) {
	// 		try {
	// 			Response r = queue.take();
	// 			if (r.isControl()) {
	// 				Message m = new Message(1, client.getIp().getHostAddress() + ";" +
	// 						client.getPort(), "", null);
	// 				m.setControl(r.getControl());
	// 				client.getPartitions().sendPartition(partition, m);
	// 			} else {
	// 				List<Command> cmds = new ArrayList<Command>();
	// 				int size = r.getCommand().getValue().length;
	// 				cmds.add(r.getCommand());
	// 				if (batch_size > 0) {
	// 					while ((r = queue.poll(500, TimeUnit.MICROSECONDS)) != null) {
	// 						cmds.add(r.getCommand());
	// 						size = size + r.getCommand().getValue().length;
	// 						if (size >= batch_size) {
	// 							break;
	// 						}
	// 					}
	// 					logger.debug("BatchSender composed #cmd " + cmds.size() + " with size " +
	// 							size + " bytes.");
	// 				}
	// 				Message m = new Message(1, client.getIp().getHostAddress() + ";" +
	// 						client.getPort(), "", cmds);
	// 				client.getPartitions().sendPartition(partition, m);
	// 			}
	// 		} catch (Exception e) {
	// 			e.printStackTrace();
	// 			Thread.currentThread().interrupt();
	// 			break;
	// 		}
	// 	}
	// 	logger.info("Batch sent");
	// }

	private final static Logger logger = Logger.getLogger(BatchSender.class);

	private final ABSender sender;

	private final Client client;

	private final BlockingQueue<Response> queue;

	private final int batch_size = 8912; // 0: disable

	public BatchSender(int ring, Client client)
			throws TTransportException, IOException, KeeperException, InterruptedException {
		this.client = client;
		sender = getThriftABSender(ring, client.getConnectMap().get(ring));
		queue = client.getSendQueues().get(ring);
	}

	public ABSender getThriftABSender(int ring, int clientID) throws TTransportException {
		String host = "127.0.0.1";
		try {
			host = new String(
					client.getZooKeeper().getData("/ringpaxos/topology" + ring + "/nodes/" + clientID, false, null));
			host = host.replaceAll("(;.*)", "");
			if (client.getZooKeeper().exists("/ringpaxos/topology" + ring + "/proposers/" + clientID, false) != null) {
				logger.debug("ABSender check for ring " + ring + ": OK!");
			} else {
				logger.warn("ABSender check for ring " + ring + ": Fail!");
			}
		} catch (KeeperException | InterruptedException e) {
			logger.error(e);
		}
		logger.debug("ThriftABSender host: " + host + ":" + (9080 + clientID));
		ABSender proposer = new ThriftABSender(host, 9080 + clientID);
		return proposer;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Response r = queue.take();
				List<Command> cmds = new ArrayList<Command>();
				int size = r.getCommand().getValue().length;
				cmds.add(r.getCommand());
				if (batch_size > 0) {
					while ((r = queue.poll(500, TimeUnit.MICROSECONDS)) != null) {
						cmds.add(r.getCommand());
						size = size + r.getCommand().getValue().length;
						if (size >= batch_size) {
							break;
						}
					}
					logger.debug("BatchSender composed #cmd " + cmds.size() + " with size " + size + " bytes.");
				}
				Message m = new Message(1, client.getIp().getHostAddress() + ";" + client.getPort(), "", cmds);
				sender.abroadcast(m);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
