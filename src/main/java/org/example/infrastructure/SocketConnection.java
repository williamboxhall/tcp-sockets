package org.example.infrastructure;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.example.domain.Event;

public class SocketConnection implements Connection {
	private final Socket socket;

	public SocketConnection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void send(Event event) {
		try {
			new PrintWriter(socket.getOutputStream(), true).println(event.raw());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			LOG.error(format("Failed to close socket: %s", e));
		}
	}
}
