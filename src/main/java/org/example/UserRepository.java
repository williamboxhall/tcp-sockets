package org.example;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRepository {
	private final Map<Integer, User> users = new HashMap<>();

	public void connect(int userId, SocketChannel socketChannel) {
		get(userId).setSocketChannel(socketChannel);
	}

	public void disconnect(int userId) {
		get(userId).setSocketChannel(null);
	}

	public User get(int userId) {
		if (!users.containsKey(userId)) {
			users.put(userId, new User(null));
		}
		return users.get(userId);
	}

	public Set<Integer> allUserIds() {
		return new HashSet<>(users.keySet());
	}
}
