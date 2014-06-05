package org.example.domain;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum EventType {
	FOLLOW("F") {
		public Set<Integer> recipientsFor(Event event, UserRepository userRepository) {
			User user = userRepository.get(event.toUserId());
			user.addFollower(event.fromUserId());
			return singleton(event.toUserId());
		}
	},
	UNFOLLOW("U") {
		@Override
		public Set<Integer> recipientsFor(Event event, UserRepository userRepository) {
			userRepository.get(event.toUserId()).removeFollower(event.fromUserId());
			return emptySet();
		}
	},
	BROADCAST("B") {
		@Override
		public Set<Integer> recipientsFor(Event event, UserRepository userRepository) {
			return new HashSet<>(userRepository.allUserIds());
		}
	},
	PRIVATE_MESSAGE("P") {
		@Override
		public Set<Integer> recipientsFor(Event event, UserRepository userRepository) {
			return singleton(event.toUserId());
		}

	},
	STATUS_UPDATE("S") {
		@Override
		public Set<Integer> recipientsFor(Event event, UserRepository userRepository) {
			return userRepository.get(event.fromUserId()).getFollowers();
		}
	};

	public abstract Set<Integer> recipientsFor(Event event, UserRepository userRepository);

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
