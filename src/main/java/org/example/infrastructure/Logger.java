package org.example.infrastructure;

import static java.lang.String.format;

public class Logger {
	public static final Logger LOG = new Logger();
	private boolean enabled = true;

	public void error(String message) {
		println(message, "ERROR");
	}

	public void info(String message) {
		println(message, "INFO");
	}

	public void debug(String message) {
		if (enabled) {
			println(message, "DEBUG");
		}
	}

	private void println(String message, String type) {
		System.out.println(format("[%s] %s", type, message));
	}
}
