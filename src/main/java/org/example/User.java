package org.example;

import java.nio.channels.SocketChannel;

public class User {
	private SocketChannel socketChannel;

	public User(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	// TODO gross
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
}
