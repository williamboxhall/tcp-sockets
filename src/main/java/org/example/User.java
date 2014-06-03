package org.example;

import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

public class User {
	private final Set<Integer> followers = new HashSet<>();
	private SocketChannel socketChannel = null;

	public User(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public User() {
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	// TODO gross
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void addFollower(int userId) {
		followers.add(userId);
	}

	public void removeFollower(int userId) {
		followers.remove(userId);
	}

	public Set<Integer> getFollowers() {
		return new HashSet<>(followers);
	}
}
