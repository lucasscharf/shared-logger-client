package ch.usi.da.paxos.storage;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import ch.usi.da.paxos.api.StableStorage;

/**
 * Name: InMemory<br>
 * Description: <br>
 * 
 * Creation date: Mar 31, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class InFile implements StableStorage {
	private Path path;
	private FileWriter writer;

	private final Map<Long, Integer> promised = new LinkedHashMap<Long, Integer>(10000, 0.75F, false) {
		private static final long serialVersionUID = -2704400128020327063L;

		protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
			return size() > 15000; // hold only 15'000 values in memory !
		}
	};

	public InFile() {
		path = Paths.get("/tmp/acceptor_storage_" + UUID.randomUUID().toString());
		if (!Files.exists(path))
			try {
				path.toFile().mkdirs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				String fileName = UUID.randomUUID().toString();
				File file = path.resolve(fileName).toFile();
				file.createNewFile();
				writer = new FileWriter(file, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private long last_trimmed_instance = 0;

	private final Map<Long, Decision> decided = new LinkedHashMap<Long, Decision>(10000, 0.75F, false) {
		private static final long serialVersionUID = -3704400228030327063L;

		protected boolean removeEldestEntry(Map.Entry<Long, Decision> eldest) {
			return size() > 15000; // hold only 15'000 values in memory !
		}
	};

	@Override
	public void putBallot(Long instance, int ballot) {
		promised.put(instance, ballot);
	}

	@Override
	public int getBallot(Long instance) {
		return promised.get(instance);
	}

	@Override
	public synchronized boolean containsBallot(Long instance) {
		return promised.containsKey(instance);
	}

	@Override
	public void putDecision(Long instance, Decision decision) {
		decided.put(instance, decision);
		
		try {
			char[] contentToSave = new String(decision.getValue().getValue()).toCharArray();
			writer.write(contentToSave);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Decision getDecision(Long instance) {
		return decided.get(instance);
	}

	@Override
	public boolean containsDecision(Long instance) {
		return decided.containsKey(instance);
	}

	@Override
	public boolean trim(Long instance) {
		last_trimmed_instance = instance;
		return true;
	}

	@Override
	public Long getLastTrimInstance() {
		return last_trimmed_instance;
	}

	@Override
	public void close() {

	}

}
