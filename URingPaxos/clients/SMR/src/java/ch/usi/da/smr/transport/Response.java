package ch.usi.da.smr.transport;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ch.usi.da.paxos.message.Control;
import ch.usi.da.smr.message.Command;

/**
 * Name: Response<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public class Response {

	private final List<Command> response = new ArrayList<Command>();

	private final CountDownLatch done = new CountDownLatch(1);
	
	private final Command cmd;
	
	private final Control control;
	
	public Response(Command cmd){
		this.cmd = cmd;
		this.control = null;
	}
	
	public Response(Control control){
		this.cmd = null;
		this.control = control;
	}

	public boolean isProccessed(){
		return (done.getCount() == 0);
	}
	
	public synchronized void setResponse(List<Command> list){
		if(!isProccessed()){
			response.addAll(list);
			done.countDown();
		}
	}
	
	public List<Command> getResponse() throws InterruptedException {
		done.await();
		synchronized (this) {
			return Collections.unmodifiableList(response);
		}
	}
	
	public Command getCommand(){
		return cmd;
	}
	
	public boolean isControl(){
		if(control != null){
			return true;
		}
		return false;
	}
	
	public Control getControl(){
		return control;
	}
	
	public List<Command> getResponse(int timeout) throws InterruptedException {
		done.await(timeout,TimeUnit.MILLISECONDS);
		synchronized (this) {
			return Collections.unmodifiableList(response);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Response [cmd=");
		builder.append(cmd);
		builder.append(", control=");
		builder.append(control);
		builder.append(", done=");
		builder.append(done);
		builder.append(", response=");
		builder.append(response);
		builder.append("]");
		return builder.toString();
	}

}
