package org.example.infrastructure;

import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.closeQuietly;

import java.io.Closeable;

public class ShutdownHooks {
	private ShutdownHooks() {
	}

	public static void ensureOnExit(final Thread thread) {
		Runtime.getRuntime().addShutdownHook(thread);
	}

	public static Thread closed(final Closeable closeable) {
		return new Thread() {
			public void run() {
				closeQuietly(closeable);
				LOG.info("closed something");
			}
		};
	}
}
