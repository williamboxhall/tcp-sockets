package org.example;

import static org.example.Connection.BLACK_HOLE;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRepository {
	private final Map<Integer, User> users = new HashMap<>();

	public void connect(int userId, SocketChannel socketChannel) {
		get(userId).setSocketChannel(new FallbackConnection(new SocketConnection(socketChannel), BLACK_HOLE));
	}

	public User get(int userId) {
		if (!users.containsKey(userId)) {
			users.put(userId, new User(BLACK_HOLE));
		}
		return users.get(userId);
	}

	public Set<Integer> allUserIds() {
		return new HashSet<>(users.keySet());
	}
}
