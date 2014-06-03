package org.example.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum EventType {
	FOLLOW("F") {
		public void informUsers(Event event, UserRepository userRepository) {
			User user = userRepository.get(event.getToUserId());
			user.addFollower(event.getFromUserId());
			user.send(event);
		}
	},
	UNFOLLOW("U") {
		@Override
		public void informUsers(Event event, UserRepository userRepository) {
			userRepository.get(event.getToUserId()).removeFollower(event.getFromUserId());
		}
	},
	BROADCAST("B") {
		@Override
		public void informUsers(Event event, UserRepository userRepository) {
			for (User user : userRepository.allUsers()) {
				user.send(event);
			}
		}
	},
	PRIVATE_MESSAGE("P") {
		@Override
		public void informUsers(Event event, UserRepository userRepository) {
			userRepository.get(event.getToUserId()).send(event);
		}
	},
	STATUS_UPDATE("S") {
		@Override
		public void informUsers(Event event, UserRepository userRepository) {
			Set<Integer> toUserIds = userRepository.get(event.getFromUserId()).getFollowers();
			for (int toUserId : toUserIds) {
				userRepository.get(toUserId).send(event);
			}
		}
	};

	public abstract void informUsers(Event event, UserRepository userRepository);

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
		return CACHE_BY_ID.get(id);
	}
}
