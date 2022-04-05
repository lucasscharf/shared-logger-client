package org.acme;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.paxos.ring.LearnerRole;
import ch.usi.da.smr.Replica;
import ch.usi.da.smr.message.Message;

/**
 * Name: Replica<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class ReplicaLoggerClient extends Replica {
	private final static Logger logger = LoggerFactory.getLogger(ReplicaLoggerClient.class);

	public ReplicaLoggerClient(String token, int ringID, int nodeID, int snapshot_modulo, String zoo_host)
			throws Exception {
		super(token, ringID, nodeID, snapshot_modulo, zoo_host);
	}

	@Override
	public void receive(Message m) {
		logger.info("Receiving command [{}] with ring [{}] from instance [{}] with id [{}]", m.getCommands(),
		m.getRing(), m.getInstnce(), m.getID());
	}

}
