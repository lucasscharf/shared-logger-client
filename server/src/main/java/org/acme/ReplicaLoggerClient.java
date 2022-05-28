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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class ReplicaLoggerClient extends Replica implements LoggerClient {
	private File file;
	private Path path;
	private BufferedInputStream in;

	private final static Logger logger = LoggerFactory.getLogger(ReplicaLoggerClient.class);

	public ReplicaLoggerClient(String token, int ringID, int nodeID, int snapshot_modulo, String zoo_host)
			throws Exception {
		super(token, ringID, nodeID, snapshot_modulo, zoo_host);
		path = Paths.get("/tmp/" + UUID.randomUUID().toString());
		// file = path.toFile();

		logger.info("Path created [{}]", path);
	}

	@Override
	public void receive(Message m) {
		logger.info("Receiving command [{}] with ring [{}] from instance [{}] with id [{}]", m.getCommands(),
				m.getRing(), m.getInstnce(), m.getID());
		// super.receive(m);

		try {
			if (!Files.exists(path))
				Files.createFile(path);

			Files.write(path,
					m.getCommands().get(0).getValue(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public void close() {
		logger.info("Clossing files");
	}

	@Override
	public List<String> getAllLogs() {
		try {
			if (!Files.exists(path))
				Files.createFile(path);

			return Files.readAllLines(path).stream().collect(Collectors.toList());
		} catch (IOException e) {
			logger.error("", e);
		}
		return new ArrayList<>();
	}
}
