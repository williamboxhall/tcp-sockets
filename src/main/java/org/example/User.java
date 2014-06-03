package org.example;

import java.nio.channels.SocketChannel;

public class User {
	private final SocketChannel socketChannel;

	public User(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
}
