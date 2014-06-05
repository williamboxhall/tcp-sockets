package org.example.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRepository {
	private final Map<Integer, User> users = new HashMap<>();
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
