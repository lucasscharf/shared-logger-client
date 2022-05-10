package org.acme;

public class LoggerConfig {
	public int ring;
	public int id;
	public int replicas;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoggerConfig [id=");
		builder.append(id);
		builder.append(", ring=");
		builder.append(ring);
		builder.append(", replicas=");
		builder.append(replicas);
		// builder.append(", url=");
		// builder.append(url);
		builder.append("]");
		return builder.toString();
	}

}
