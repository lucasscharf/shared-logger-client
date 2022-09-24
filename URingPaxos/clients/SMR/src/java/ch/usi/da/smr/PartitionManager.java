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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import ch.usi.da.paxos.api.PaxosRole;
import ch.usi.da.paxos.lab.DummyWatcher;
import ch.usi.da.paxos.ring.RingDescription;
import ch.usi.da.smr.message.Command;
import ch.usi.da.smr.message.Message;
import ch.usi.da.smr.transport.ABListener;
import ch.usi.da.smr.transport.ABSender;
import ch.usi.da.smr.transport.RawABListener;
import ch.usi.da.smr.transport.RawABSender;
import ch.usi.da.smr.transport.ThriftABListener;
import ch.usi.da.smr.transport.ThriftABSender;
import ch.usi.da.smr.transport.UDPListener;

/**
 * Name: PartitionManager<br>
 * Description: <br>
 * 
 * Creation date: Aug 28, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class PartitionManager implements Watcher {
	
	private final static Logger logger = Logger.getLogger(PartitionManager.class);
	
	private final String zoo_host;
	
	private ZooKeeper zoo;
	
	private final String prefix = "/smr";
	
	private final String path = prefix + "/partitions";
	
	private final SortedMap<Integer, Integer> circle = new TreeMap<Integer, Integer>();

	private final List<Partition> partitions = new ArrayList<Partition>();
	
	private final Map<String,ABSender> proposers = new HashMap<String,ABSender>();

	private final Map<Integer,List<Integer>> loadbalancer = new HashMap<Integer,List<Integer>>();
	
	private final int signalPort = 40444;
	
	private DatagramSocket signalSender;
	
	private UDPListener signalReceiver;
	
	private final Map<Command,CountDownLatch> signals = new HashMap<Command,CountDownLatch>();
	
	private final Map<Command,List<String>> wait = new HashMap<Command,List<String>>();
	
	int position = 1;

	private int global_ring = 16;
	
	private Replica replica = null;
	
	private final Map<Integer,Integer> connectMap;

	public PartitionManager(String zoo_host) {
		this.zoo_host = zoo_host;
		this.connectMap = null;
	}

	public PartitionManager(String zoo_host,Map<Integer,Integer> connectMap) {
		this.zoo_host = zoo_host;
		this.connectMap = connectMap;
	}

	public void init() throws KeeperException, InterruptedException, IOException {
		logger.info("Init PartitionManager");
		zoo = new ZooKeeper(zoo_host,3000,new DummyWatcher());
		zoo.register(this);
		// create path
		String p = "";
		for(String s : path.split("/")){
			if(s.length() > 0){
				p = p + "/" + s;
				if(zoo.exists(p,false) == null){
					zoo.create(p,null,Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				}
			}
		}
		// set default global ring = 16
		if(zoo.exists(path + "/all",false) == null){
			zoo.create(path + "/all","16".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
		}
		readPartitions();
		readLBRings();
		try {
			signalSender = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public Partition register(int replicaID, int ringID, InetAddress ip, String token){
		try {
			signalReceiver = new UDPListener(signalPort);
			signalReceiver.registerReceiver(this);
			Thread t = new Thread(signalReceiver);
			t.setName("SignalReceiver");
			t.start();
		} catch (SocketException e) {
			logger.error("Error staritng signal receiver!",e);
		}
		try {
			if(zoo.exists(path + "/" + token,false) == null){
				zoo.create(path + "/" + token,Integer.toString(ringID).getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}else{
				zoo.setData(path + "/" + token, Integer.toString(ringID).getBytes(), -1);
			}
			byte[] data = ip.getHostAddress().getBytes();
			zoo.create(path + "/" + token + "/" + Integer.toString(replicaID),data,Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
		} catch (KeeperException e) {
			logger.error(e);
		} catch (InterruptedException e) {
		}
		readPartitions();
		for(Partition p : partitions){
			if(p.getID().equals(token)){
				return p;
			}
		}
		return null;
	}

	public void deregister(int replicaID, String token){
		try {
			zoo.delete(path + "/" + token + "/" + Integer.toString(replicaID),-1);
			zoo.delete(path + "/" + token,-1);
		} catch (KeeperException | InterruptedException e) {
		}
	}
	
	public List<Partition> getPartitions(){
		return Collections.unmodifiableList(partitions);
	}

	public List<String> getReplicas(String token){
		List<String> replicas = new ArrayList<String>();
		try {
			List<String> ls = zoo.getChildren(path + "/" + token, true);
			for(String s : ls){
				replicas.add(new String(zoo.getData(path + "/" + token + "/" + s, false, null)));
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return replicas;
	}

	public List<String> getReplicaIDs(String token){
		List<String> replicas = new ArrayList<String>();
		try {
			return zoo.getChildren(path + "/" + token, true);
		} catch (Exception e) {
			logger.error(e);
		}
		return replicas;
	}

	public SortedMap<Integer, Integer> getCircle(){
		return Collections.unmodifiableSortedMap(circle);
	}

	public int getGlobalRing(){
		return global_ring;
	}

	public int getRing(String key){
		return getRing(MurmurHash.hash32(key));
	}
	
	private int getRing(int hash){
		if (circle.isEmpty()) {
			return -1;
		}
		if (!circle.containsKey(hash)) {
			SortedMap<Integer, Integer> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return circle.get(hash);
	}

	public int getPartition(String key){
		int hash = MurmurHash.hash32(key);
		if (circle.isEmpty()) {
			return -1; // the "all" partitionv
		}
		if (!circle.containsKey(hash)) {
			SortedMap<Integer, Integer> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return hash;
	}

	public ABListener getRawABListener(int replicaID, int[] paxosRings) throws IOException, KeeperException, InterruptedException {
		List<PaxosRole> role = new ArrayList<PaxosRole>();
		role.add(PaxosRole.Learner);
		List<RingDescription> rings = new ArrayList<RingDescription>();

		for (int ring : paxosRings) {
			rings.add(new RingDescription(ring,role));
		}

		logger.debug("Create RawABListener " + rings);
		Thread.sleep(1000); // wait until PartitionManger is ready
		return new RawABListener(replicaID,zoo_host,rings);
	}

	public ABListener getThriftABListener(int ring, int replicaID) throws TTransportException {
		String host = "127.0.0.1";
		try {
			host = new String(zoo.getData("/ringpaxos/topology" + ring + "/nodes/" + replicaID,false, null));
			host = host.replaceAll("(;.*)","");
			// Sanity check: is replicaID learner in ring(partition) && global_ring
			if(zoo.exists("/ringpaxos/topology" + ring + "/learners/" + replicaID,false) != null &&
			   zoo.exists("/ringpaxos/topology" + global_ring + "/learners/" + replicaID,false) != null	){
				logger.debug("ABListener check for ring " + ring + " and " + global_ring + ": OK!");
			}else{
				logger.warn("ABListener check for ring " + ring + " and " + global_ring + ": Fail!");
			}			
		} catch (KeeperException | InterruptedException e) {
			logger.error(e);
		}
		logger.debug("ThriftABListener host: " + host + ":" + (9090+replicaID));
		return new ThriftABListener(host,9090+replicaID);
	}

	public ABSender getRawABSender(int ring, int clientID) throws IOException, KeeperException, InterruptedException {
		if(proposers.containsKey(ring + "-" + clientID)){
			return proposers.get(ring + "-" + clientID);
		}else{
			List<PaxosRole> role = new ArrayList<PaxosRole>();
			role.add(PaxosRole.Proposer);
			List<RingDescription> rings = new ArrayList<RingDescription>();
			rings.add(new RingDescription(ring, role));
			logger.debug("RawABSender " + rings);
			ABSender proposer = new RawABSender(clientID, zoo_host, rings);
			proposers.put(ring + "-" + clientID, proposer);
			return proposer;
		}
	}

	public ABSender getThriftABSender(int ring, int clientID) throws TTransportException {
		if(proposers.containsKey(ring + "-" + clientID)){
			return proposers.get(ring + "-" + clientID);
		}else{
			String host = "127.0.0.1";
			try {
				host = new String(zoo.getData("/ringpaxos/topology" + ring + "/nodes/" + clientID,false, null));
				host = host.replaceAll("(;.*)","");
				if(zoo.exists("/ringpaxos/topology" + ring + "/proposers/" + clientID,false) != null){
					logger.debug("ABSender check for ring " + ring + ": OK!");
				}else{
					logger.warn("ABSender check for ring " + ring + ": Fail!");
				}			
			} catch (KeeperException | InterruptedException e) {
				logger.error(e);
			}
			logger.debug("ThriftABSender host: " + host + ":" + (9080+clientID));
			ABSender proposer = new ThriftABSender(host,9080+clientID);
			proposers.put(ring + "-" + clientID, proposer);
			return proposer;
		}
	}

	public synchronized void sendPartition(int partition,Message m) throws Exception {
		int ring = loadbalancer.get(partition).get(position-1);
		position++;
		if(position > loadbalancer.get(partition).size()){
			position = 1;
		}
		ABSender sender = getThriftABSender(ring,connectMap.get(ring));
		sender.abroadcast(m);
	}

	public synchronized void sendRing(int ring,Message m) throws Exception {
		ABSender sender = getThriftABSender(ring,connectMap.get(ring));
		sender.abroadcast(m);
	}
	
	@Override
	public void process(WatchedEvent event) {
		if(event.getType() == EventType.NodeChildrenChanged && event.getPath().startsWith(path)){
			readPartitions();
			readLBRings();
		}
		if(event.getType() == EventType.NodeDataChanged && event.getPath().startsWith(path)){
			readLBRings();
		}
	}

	public void registerPartitionChangeNotifier(Replica replica) {
		this.replica = replica;
	}

	private synchronized void readLBRings(){
		try {
			List<String> ls = zoo.getChildren(path, true);
			for(String s : ls){
				int part = -1;
				if(!s.equals("all")){
					part = Integer.parseInt(s,16);
				}
				String rstring = new String(zoo.getData(path + "/" + s, true, null));
				List<Integer> rings = new ArrayList<Integer>();
				String[] ra = rstring.split(",");
				for(String r : ra){
					rings.add(Integer.parseInt(r));
				}
				loadbalancer.put(part, rings);
			}
			position = 1;
			logger.info("PartitionManger changed load balancer: " + loadbalancer);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private synchronized void readPartitions(){
		partitions.clear();
		circle.clear();
		try {
			List<String> ls = zoo.getChildren(path, true);
			for(String s : ls){
				String rstring = new String(zoo.getData(path + "/" + s, true, null));
				String rings[] = rstring.split(",");
				int ring = Integer.parseInt(rings[0]);
				if(s.equals("all")){
					String sa = new String(zoo.getData(path + "/all", true, null));
					String[] ra = sa.split(",");
					global_ring = Integer.parseInt(ra[0]);
				}else{
					circle.put(Integer.parseInt(s,16),ring);
				}
			}
			for(Entry<Integer, Integer> e : circle.entrySet()){
				String id = Integer.toString(e.getKey(),16);
				int high = e.getKey();
				SortedMap<Integer, Integer> headMap = circle.headMap(high);
				int low = headMap.isEmpty() ? circle.lastKey() : headMap.lastKey();
				Partition p = new Partition(id,e.getValue(),low+1,high);
				partitions.add(p);
				if(replica != null && replica.token.equals(id)){
					replica.setPartition(p);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void main(String args[]) throws KeeperException, InterruptedException, IOException{
		final int replicaID = 1;
		PartitionManager partitions = new PartitionManager("127.0.0.1:2181");
		partitions.init();

		// two partitions
		partitions.register(replicaID,1,InetAddress.getLocalHost(),"0");
		partitions.register(replicaID,2,InetAddress.getLocalHost(),"7FFFFFFF");

		// four partitions
		//partitions.register(replicaID,3,InetAddress.getLocalHost(),"3FFFFFFF");
		//partitions.register(replicaID,4,InetAddress.getLocalHost(),"-3FFFFFFF");		

		// three partitions
		//partitions.register(replicaID,1,InetAddress.getLocalHost(),"0");
		//partitions.register(replicaID,2,InetAddress.getLocalHost(),"55555554");
		//partitions.register(replicaID,2,InetAddress.getLocalHost(),"-55555554");
		
		List<String> ps = new ArrayList<String>();
		for(Partition p : partitions.getPartitions()){
			ps.add(p.getID());
			System.out.println(p + " size:" + (p.getHigh()-p.getLow()));
		}
		
		/*Thread.sleep(1000);
		partitions.deregister(replicaID,"0");
		Thread.sleep(1000);
		System.out.println("---------------------------------------------");
		for(Partition p : partitions.getPartitions()){
			System.out.println(p);
		}*/

		//System.err.println(partitions.getRing(1));

		for(String p : ps){
			partitions.deregister(replicaID,p);
		}
	}

	public void singal(String token, Command c) {
		//TODO: set only subset
		List<String> await = new ArrayList<String>();
		String[] tokens = new String(c.getValue()).split(";");
		if(tokens.length > 1){
			for(int i=1;i<tokens.length;i++){
				await.add(tokens[i]);
			}
		}else{
			for(Partition p : getPartitions()){
				await.add(p.getID());
			}
		}
		logger.info("Getrange wait for partitions: " + await);
		wait.put(c,await);
		signals.put(c, new CountDownLatch(await.size()));
		List<Command> cmds = new ArrayList<Command>();
		cmds.add(c);
		Message m = new Message(c.getID(),token,"",cmds);
		for(Partition p : getPartitions()){
			for(String replica : getReplicas(p.getID())){
				try {
					InetAddress address = InetAddress.getByName(replica);
					byte[] buffer = Message.toByteArray(m);
					DatagramPacket packet = new DatagramPacket(buffer,0,buffer.length,address,signalPort);
					signalSender.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}

	public boolean waitSignal(Command c) {
		boolean ret = false;
		try {
			ret = signals.get(c).await(1,TimeUnit.SECONDS);
			signals.remove(c);
		} catch (InterruptedException e) {
		}
		return ret;
	}

	public void receive(Message m) {
		Command c = m.getCommands().get(0);
		if(c != null && signals.containsKey(c)){
			if(wait.get(c).contains(m.getFrom())){
				wait.get(c).remove(m.getFrom());
				signals.get(m.getCommands().get(0)).countDown();
				if(wait.get(c).isEmpty()){
					wait.remove(c);
				}
			}
		}
	}

}
