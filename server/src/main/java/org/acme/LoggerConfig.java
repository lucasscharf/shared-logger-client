package org.acme;

public class LoggerConfig {
	public String ring;
	public int id;
	public int replicas;
	public String pathPrefix = "/mnt/disk1";

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoggerConfig [id=");
		builder.append(id);
		builder.append(", ring=");
		builder.append(ring);
		builder.append(", replicas=");
		builder.append(replicas);
		builder.append(", pathPrefix=");
		builder.append(pathPrefix);
		builder.append("]");
		return builder.toString();
	}

}
