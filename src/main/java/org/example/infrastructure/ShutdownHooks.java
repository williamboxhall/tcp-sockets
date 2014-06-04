package org.example.infrastructure;

import java.io.Closeable;
import java.io.IOException;

public class ShutdownHooks {
	private ShutdownHooks() {
	}

	public static void ensure(final Thread thread) {
		Runtime.getRuntime().addShutdownHook(thread);
	}

	public static Thread closed(final Closeable closeable) {
		return new Thread() {
			public void run() {
				try {
					closeable.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
