package org.example.domain;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.example.infrastructure.Logger.LOG;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.example.infrastructure.ConnectionFactory;

public class UserRepository {
	private final Map<Integer, User> users = new HashMap<>();
	private final UserFactory userFactory;
	private final ConnectionFactory connectionFactory;

	public UserRepository(UserFactory userFactory, ConnectionFactory connectionFactory) {
		this.userFactory = userFactory;
		this.connectionFactory = connectionFactory;
	}

	public void connect(int userId, SocketChannel socketChannel) {
		get(userId).updateConnection(connectionFactory.createFor(socketChannel, valueOf(userId)));
		LOG.info(format("User %s connected", userId));
	}

	public User get(int userId) {
		if (!users.containsKey(userId)) {
			users.put(userId, userFactory.create());
		}
		return users.get(userId);
	}

	public Set<User> allUsers() {
		return new HashSet<>(users.values());
	}
}
