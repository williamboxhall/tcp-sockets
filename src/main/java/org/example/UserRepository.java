package org.example;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
	private final Map<Integer, User> users = new HashMap<>();

	public void connect(int userId, SocketChannel socketChannel) {
		get(userId).setSocketChannel(socketChannel);
	}

	public User get(int userId) {
		if (!users.containsKey(userId)) {
			users.put(userId, new User(null));
		}
		return users.get(userId);
	}
}
