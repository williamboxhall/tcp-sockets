package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;

public class SocketConnection implements Connection {
	private final SocketChannel socketChannel;

	public SocketConnection(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void send(String message) {
		try {
			new PrintWriter(socketChannel.socket().getOutputStream(), true).println(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
