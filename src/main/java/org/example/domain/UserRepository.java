package org.example.domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
	private final Map<Integer, User> users = new ConcurrentHashMap<>(); // TODO read more about this
	private final UserFactory userFactory;

	public UserRepository(UserFactory userFactory) {
		this.userFactory = userFactory;
	}

	public User get(int userId) {
		if (!users.containsKey(userId)) {
			users.put(userId, userFactory.create());
		}
		return users.get(userId);
	}

	public Set<Integer> allUserIds() {
		return new HashSet<>(users.keySet());
	}

	public static class UserFactory {
		public User create() {
			return new User();
		}
	}
}
