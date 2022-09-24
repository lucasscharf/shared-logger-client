// package ch.usi.da.smr;


// import static org.junit.Assert.assertEquals;

// import java.io.File;
// import java.net.InetSocketAddress;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.apache.log4j.Level;
// import org.apache.log4j.Logger;
// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;

// import com.sun.net.httpserver.HttpServer;

// import ch.usi.da.smr.message.Command;
// import ch.usi.da.smr.message.CommandType;
// import ch.usi.da.smr.message.Message;
// import ch.usi.da.smr.recovery.DfsRecovery;
// import ch.usi.da.smr.recovery.HttpRecovery;
// import ch.usi.da.smr.transport.Receiver;
// import ch.usi.da.smr.transport.UDPListener;

// public class ReplicaTest implements Receiver {

// 	Logger logger = Logger.getLogger("ch.usi.da");
// 	List<Message> received;
// 	Replica replica;
// 	UDPListener udp;
	
// 	@Before 
// 	public void initialize() throws Exception {
// 		logger.setLevel(Level.FATAL);
		
// 		new File(HttpRecovery.state_file).delete();
// 		new File(HttpRecovery.snapshot_file).delete();
// 		new File("/tmp/smr/0/1/" + DfsRecovery.state_file).delete();
// 		new File("/tmp/smr/0/1/" + DfsRecovery.snapshot_file).delete();
// 		new File("/tmp/smr/0/2/" + DfsRecovery.state_file).delete();
// 		new File("/tmp/smr/0/2/" + DfsRecovery.snapshot_file).delete();
				
// 		replica = new Replica("0",1,1,0,"localhost:2181");
// 		replica.start();
		
// 		received = new ArrayList<Message>();
// 		udp = new UDPListener(1234);
// 		udp.registerReceiver(this);
// 		Thread t = new Thread(udp);
// 		t.start();
// 	}
	
// 	@After 
// 	public void close() {
//         replica.close();
//         udp.close();
// 	}

// 	@Test
// 	public void applyCmd() throws Exception {
// 		// put
// 		List<Command> cmd = new ArrayList<Command>();
// 		cmd.add(new Command(1, CommandType.PUT, "test", "Value".getBytes()));
// 		Message m = new Message(1,"127.0.0.1;1234","", cmd);
// 		m.setInstance(1);
// 		m.setRing(1);
// 		replica.receive(m);

// 		// get
// 		cmd = new ArrayList<Command>();
// 		cmd.add(new Command(2, CommandType.GET, "test",new byte[0]));
// 		m = new Message(2,"127.0.0.1;1234","", cmd);
// 		m.setInstance(2);
// 		m.setRing(1);
// 		replica.receive(m);

// 		// wait response
// 		Command response = null;
// 		for(int i=0;i<5;i++){
// 			Thread.sleep(1000);
// 			for(Message r : received){
// 				for(Command c : r.getCommands()){
// 					if(c.getID() == 2){
// 						response = c;
// 						break;
// 					}
// 				}
// 			}
// 		}
// 		assertEquals("Value",new String(response.getValue()));
// 	}

// 	@Test
// 	public void isReady() throws Exception {
// 		assertEquals(false,replica.is_ready(1,2L));
// 		assertEquals(true,replica.is_ready(1,1L));
// 	}

// 	@Test
// 	public void newerState() throws Exception {
// 		Map<Integer,Long> state = new HashMap<Integer,Long>();
// 		Map<Integer,Long> nstate = new HashMap<Integer,Long>();

// 		state.put(1,1L);
// 		nstate.put(1,1L);
// 		assertEquals(false,Replica.newerState(nstate, state));
		
// 		state.put(1,1L);
// 		nstate.put(1,2L);
// 		assertEquals(true,Replica.newerState(nstate, state));

// 		state.put(1,1L);
// 		nstate.put(1,1L);
// 		state.put(16,2L);
// 		nstate.put(16,3L);
// 		assertEquals(true,Replica.newerState(nstate, state));

// 		state.put(1,1L);
// 		nstate.put(1,1L);
// 		state.put(16,3L);
// 		nstate.put(16,2L);
// 		assertEquals(false,Replica.newerState(nstate, state));

// 		state.put(1,1L);
// 		nstate.put(1,2L);
// 		state.put(16,3L); // should not happen, since round-robin start in lowest ring
// 		nstate.put(16,2L);
// 		assertEquals(true,Replica.newerState(nstate, state));

// 		state.clear();
// 		nstate.clear();
// 		assertEquals(false,Replica.newerState(nstate, state));
		
// 		state.put(0,0L); // special case: ring 0 means not global ring	
// 		state.put(1,1L);
// 		nstate.put(1,2L);
// 		assertEquals(true,Replica.newerState(nstate, state));
// 	}

// 	@Test
// 	public void copyOnWrite() throws Exception {
// 		Map<Integer,Long> state = new HashMap<Integer,Long>();
// 		state.put(1,1L);
// 		Map<Integer,Long> nstate = new HashMap<Integer,Long>(state);
		
// 		assertEquals(state.get(1),nstate.get(1));
		
// 		nstate.put(1,2L);
// 		assertEquals(true,state.get(1) == 1L);
// 		assertEquals(true,nstate.get(1) == 2L);		
		
// 	}

// 	@Override
// 	public void receive(Message m) {
// 		received.add(m);
// 	}

// 	@Override
// 	public boolean is_ready(Integer ring, Long instance) {
// 		return true;
// 	}
	
// }
