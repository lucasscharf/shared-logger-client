package ch.usi.da.paxos.ring;
/* 
 * Copyright (c) 2013 Universit√† della Svizzera italiana (USI)
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
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import ch.usi.da.paxos.TopologyManager;
import ch.usi.da.paxos.api.PaxosNode;


/**
 * Name: RingManager<br>
 * Description: <br>
 * 
 * Creation date: Aug 12, 2012<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class RingManager extends TopologyManager {
	
	private final static Logger logger = Logger.getLogger(RingManager.class);

	private volatile int last_acceptor = 0;

	/**
	 * @param node
	 * @param ringID
	 * @param addr
	 * @param zoo
	 */
	public RingManager(PaxosNode node,int ringID,InetSocketAddress addr,ZooKeeper zoo) {
		super(node,ringID,addr,zoo,"/ringpaxos");
	}

	/**
	 * @param node
	 * @param ringID
	 * @param addr
	 * @param zoo
	 * @param prefix zookeeper prefix
	 */
	public RingManager(PaxosNode node, int ringID,InetSocketAddress addr,ZooKeeper zoo,String prefix) {
		super(node,ringID,addr,zoo,prefix);
	}

	/**
	 * @param nodeID
	 * @param ringID
	 * @param addr
	 * @param zoo
	 * @param prefix zookeeper prefix
	 */
	public RingManager(int nodeID, int ringID,InetSocketAddress addr,ZooKeeper zoo,String prefix) {
		super(nodeID,ringID,addr,zoo,prefix);
	}

	/**
	 * Init the ring manger
	 * 
	 * (we need this init() because of the "this" references) 
	 * 
	 * @throws IOException
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void init() throws IOException, KeeperException, InterruptedException {
		network = new NetworkManager(this);
		zoo.register(this);
		getConfig();
		network.startServer();
		//Thread.sleep(1000); // give server time to start-up
		registerNode();		
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	protected void getConfig() throws KeeperException, InterruptedException {
		super.getConfig();
		
		// get last_acceptor
		try {
			List<String> l = zoo.getChildren(path + "/" + acceptor_path, true);
			int max = 0;
			for(String s : l){
				int i = Integer.valueOf(s);
				if(i > max){
					max = i; 
				}
			}
			last_acceptor = max;
		} catch (NoNodeException e){
		}
	}

	/**
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	protected void registerNode() throws KeeperException, InterruptedException {
		super.registerNode();
	}
	private void notifyRingChanged(){
		InetSocketAddress saddr = getNodeAddress(getRingSuccessor(nodeID));
		logger.info("RingManager ring " + topologyID + " changed: " + nodes + " (succsessor: " + getRingSuccessor(nodeID) + " at " + saddr + ")");
		if(saddr != null && currentConnection == null || !currentConnection.equals(saddr)){
			network.disconnectClient();
			network.connectClient(saddr);
			currentConnection = saddr;
		}
	}
	
	private void notifyNewCoordinator(){
		logger.info("RingManger this node is the new coordinator for ring " + topologyID + "!");
		Thread c = new Thread(new CoordinatorRole(this));
		c.setName("Coordinator");
		c.start();
	}
		
	/**
	 * @return the ring
	 */
	public List<Integer> getRing(){
		return Collections.unmodifiableList(nodes);
	}
	
	/**
	 * @return the ring id
	 */
	public int getRingID(){
		return topologyID;
	}
	
	/**
	 * @param id 
	 * @return the node id of the ring successor
	 */
	public int getRingSuccessor(int id){
		int pos = nodes.indexOf(new Integer(id));
		if(pos+1 >= nodes.size()){
			return nodes.get(0);
		}else{
			return nodes.get(pos+1);
		}
	}
	
	/**
	 * @param id 
	 * @return the node id of the ring predecessor
	 */
	public int getRingPredecessor(int id){
		int pos = nodes.indexOf(new Integer(id));
		if(pos-1 < 0){
			return nodes.get(nodes.size()-1);
		}else{
			return nodes.get(pos-1);
		}
	}
	
	/**
	 * @return the id of the last acceptor in the ring
	 */
	public int getLastAcceptor(){
		return last_acceptor;
	}
	
	@Override
	public void process(WatchedEvent event) {
		int old_coordinator = coordinator;
		super.process(event);
		
		// ls nodes to form the ring
		try {
			if(event.getType() == EventType.NodeChildrenChanged){
				if(event.getPath().startsWith(path + "/" + id_path)){
					nodes.clear();
					List<String> l = zoo.getChildren(path + "/" + id_path, true);
					for(String s : l){
						nodes.add(Integer.valueOf(s));
					}
					Collections.sort(nodes);
					notifyRingChanged();
				}else if(event.getPath().startsWith(path + "/" + acceptor_path)){
					List<String> l = zoo.getChildren(path + "/" + acceptor_path, true);
					int min = nodeID+1;
					int max = 0;
					for(String s : l){
						int i = Integer.valueOf(s);
						if(i < min){
							min = i;
						}
						if(i > max){
							max = i; 
						}
					}
					last_acceptor = max;
					coordinator = min;
					if(nodeID == min && old_coordinator != coordinator){
						notifyNewCoordinator();
					}
				}
			}
		} catch (KeeperException e) {
			logger.error(e);
		} catch (InterruptedException e) {
		}		
	}

}
