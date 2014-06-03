package org.example.domain;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.example.infrastructure.Logger.LOG;

import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.example.infrastructure.ConnectionFactory;

public class UserRepository {
	private final Map<Integer, User> users = new ConcurrentHashMap<>(); // TODO read more about this
	private final UserFactory userFactory;
	private final ConnectionFactory connectionFactory;

	public UserRepository(UserFactory userFactory, ConnectionFactory connectionFactory) {
		this.userFactory = userFactory;
		this.connectionFactory = connectionFactory;
	}

	public void connect(int userId, Socket socket) { // TODO take connection here instead?
		get(userId).updateConnection(connectionFactory.createFor(socket, valueOf(userId)));
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

	public void disconnectAll() {
		for (User user : allUsers()) {
			user.disconnect();
		}
	}
}
