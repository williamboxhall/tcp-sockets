package org.example.infrastructure;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;

import org.example.domain.Event;

public class SocketConnection implements Connection {
	private final SocketChannel socketChannel;

	public SocketConnection(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void send(Event event) {
		try {
			new PrintWriter(socketChannel.socket().getOutputStream(), true).println(event.raw());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
