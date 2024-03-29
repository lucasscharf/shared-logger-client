package ch.usi.da.smr.message;

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

/**
 * Name: CommandType<br>
 * Description: <br>
 * 
 * Creation date: Mar 12, 2013<br>
 * $Id$
 * 
 * @author Samuel Benz benz@geoid.ch
 */
public enum CommandType {
	GET(1),
	PUT(3),
	DELETE(4),
	RESPONSE(5);
	
	private final int id;
	private CommandType(int id) {
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public static CommandType fromId(int id){
		for (CommandType r: values()){
			if (r.id == id) {
				return r;
			}
		}
		throw new RuntimeException("CommandType " + id + "does not exist!");
	}

}
