package org.example.domain;

import java.util.HashMap;
import java.util.Map;

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

	public static class UserFactory {
		public User create() {
			return new User();
		}
	}
}
