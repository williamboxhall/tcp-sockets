package org.example.domain;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum EventType {
	FOLLOW("F") {
		Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository) {
			User user = userRepository.get(toUser);
			user.addFollower(fromUser);
			return singleton(toUser);
		}
	},
	UNFOLLOW("U") {
		@Override
		Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository) {
			userRepository.get(toUser).removeFollower(fromUser);
			return emptySet();
		}
	},
	BROADCAST("B") {
		@Override
		Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository) {
			return userRepository.allUserIds();
		}
	},
	PRIVATE_MESSAGE("P") {
		@Override
		Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository) {
			return singleton(toUser);
		}

	},
	STATUS_UPDATE("S") {
		@Override
		Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository) {
			return userRepository.get(fromUser).getFollowers();
		}
	};

	abstract Set<Integer> recipientsFor(Integer fromUser, Integer toUser, UserRepository userRepository);

	private static final Map<String, EventType> CACHE_BY_ID = new HashMap<>();

	static {
		for (EventType value : values()) {
			CACHE_BY_ID.put(value.id, value);
		}
	}

	private final String id;

	private EventType(String id) {
		this.id = id;
	}

	static EventType byId(String id) {
		return checkNotNull(CACHE_BY_ID.get(id));
	}

	public static <T> T checkNotNull(T arg) {
		if (arg == null) {
			throw new IllegalArgumentException();
		}
		return arg;
	}
}
