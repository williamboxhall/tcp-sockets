package org.example.infrastructure;

import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.closeQuietly;

import java.io.Closeable;

public class ShutdownHooks {
	private ShutdownHooks() {
	}

	public static <T extends Closeable> T ensureClosedOnExit(final T closeable) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOG.debug("Closing " + closeable.toString());
				closeQuietly(closeable);
			}
		});
		return closeable;
	}
}
