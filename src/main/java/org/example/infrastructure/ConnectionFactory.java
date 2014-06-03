package org.example.infrastructure;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;

import java.net.Socket;

import org.example.domain.Event;

public class ConnectionFactory {
	public static Connection BLACK_HOLE = new Connection() {
		@Override
		public void send(Event event) {
			LOG.debug(format("Dropped event %s", event));
		}
	};

	public Connection createFor(Socket socket, String name) {
		return new FallbackConnection(new SocketConnection(socket), BLACK_HOLE, name);
	}
}
