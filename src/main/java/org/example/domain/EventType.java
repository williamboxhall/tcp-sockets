package org.example.domain;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum EventType {
	FOLLOW("F") {
		Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository) {
			userRepository.get(toUser).addFollower(fromUser);
			return singleton(toUser);
		}
	},
	UNFOLLOW("U") {
		@Override
		Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository) {
			userRepository.get(toUser).removeFollower(fromUser);
			return emptySet();
		}
	},
	BROADCAST("B") {
		@Override
		Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository) {
			return allConnected;
		}
	},
	PRIVATE_MESSAGE("P") {
		@Override
		Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository) {
			return singleton(toUser);
		}
	},
	STATUS_UPDATE("S") {
		@Override
		Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository) {
			return userRepository.get(fromUser).followers();
		}
	};

	abstract Set<Integer> updateAndReturnRecipients(Integer fromUser, Integer toUser, Set<Integer> allConnected, UserRepository userRepository);

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
