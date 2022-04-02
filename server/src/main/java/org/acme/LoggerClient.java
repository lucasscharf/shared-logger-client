package org.acme;

import java.util.List;

public interface LoggerClient extends AutoCloseable {
	List<String> getAllLogs();
}
