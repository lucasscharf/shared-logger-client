package ch.usi.da.paxos.ring;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import ch.usi.da.paxos.api.ConfigKey;
import ch.usi.da.paxos.api.Learner;
import ch.usi.da.paxos.api.PaxosRole;
import ch.usi.da.paxos.storage.Decision;

/**
 * Name: MultiLearnerRole<br>
 * Description: <br>
 * 
 * Deprecated: use ElasticLearnerRole
 * Just here to make the code downward compatible!
 * 
 * Creation date: Mar 04, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
@Deprecated
public class MultiLearnerRole extends Role implements Learner {

	private final static Logger logger = Logger.getLogger(MultiLearnerRole.class);
	
	private final Map<Integer,RingDescription> ringmap = new HashMap<Integer,RingDescription>();
	
	private final List<Integer> ring = new ArrayList<Integer>();
	
	private final int maxRing = 20;
	
	private final BlockingQueue<Decision> values = new LinkedBlockingQueue<Decision>(); 
	
	private final LearnerRole[] learner = new LearnerRole[maxRing];
	
	private int M = 1;
		
	private int deliverRing;
	
	private final long[] skip_count = new long[maxRing];
	
	private boolean deliver_skip_messages = false;

	/**
	 * @param rings a list of rings
	 */
	@Deprecated
	public MultiLearnerRole(List<RingDescription> rings) {
		int minRing = maxRing+1;
		for(RingDescription ring : rings){
			if(ring.getRingID() < minRing){
				minRing = ring.getRingID();
			}
			this.ring.add(ring.getRingID());
			this.ringmap.put(ring.getRingID(),ring);
		}
		Collections.sort(ring);
		RingManager firstRing = rings.get(0).getRingManager();
		deliverRing = minRing;
		logger.debug("MultiRingLearner initial deliverRing=" + deliverRing);
		if(firstRing.getConfiguration().containsKey(ConfigKey.deliver_skip_messages)){
			if(firstRing.getConfiguration().get(ConfigKey.deliver_skip_messages).contains("1")){
				deliver_skip_messages = true;
			}
			logger.info("MultiRingLearner deliver_skip_messages: " + (deliver_skip_messages ? "enabled" : "disabled"));
		}
	}

	@Override
	public void run() {
		for(Entry<Integer,RingDescription> e : ringmap.entrySet()){
			// create learners
			RingManager ring = e.getValue().getRingManager();
			Role r = new LearnerRole(ring);
			learner[e.getKey()] = (LearnerRole) r;
			logger.debug("MultiRingLeaner register role: " + PaxosRole.Learner + " at node " + ring.getNodeID() + " in ring " + ring.getRingID());
			ring.registerRole(PaxosRole.Learner);		
			Thread t = new Thread(r);
			t.setName(PaxosRole.Learner + "-" + e.getKey());
			t.start();
			skip_count[e.getKey()] = 0;
		}
		int count = 0;
		while(true){
			try{
				if(skip_count[deliverRing] > 0){
					count++;
					skip_count[deliverRing]--;
					//logger.debug("MultiRingLearner " + ringmap.get(deliverRing).getNodeID() + " ring " + deliverRing + " skiped a value (" + skip_count[deliverRing] + " skips left)");
				}else{
					Decision d = learner[deliverRing].getDecisions().take();
					if(d.getValue() != null && d.getValue().isSkip()){
						// skip message
						try {
							long skip = Long.parseLong(new String(d.getValue().getValue()));
							skip_count[deliverRing] = skip_count[deliverRing] + skip;
						}catch (NumberFormatException e) {
							logger.error("MultiRingLearner received incomplete SKIP message! -> " + d,e);
						}
						if(deliver_skip_messages){
							values.add(d);
						}
					}else{
						count++;
						// learning an actual proposed value
						values.add(d);
					}
				}
				if(count >= M){
					count = 0;
					deliverRing = getRingSuccessor(deliverRing);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;				
			}
		}
	}

	private int getRingSuccessor(int id){
		int pos = ring.indexOf(new Integer(id));
		if(pos+1 >= ring.size()){
			return ring.get(0);
		}else{
			return ring.get(pos+1);
		}
	}

	@Override
	public BlockingQueue<Decision> getDecisions() {
		return values;
	}

	public void setSafeInstance(Integer ring, Long instance) {
		learner[ring].setSafeInstance(ring,instance);
	}

}
