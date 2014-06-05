package org.example.domain;

import static org.example.domain.EventType.checkNotNull;

import java.util.Set;

public class Event {
	private final String raw;
	private final long sequenceNumber;
	private final EventType type;
	private final Integer fromUser;
	private final Integer toUser;

	public Event(String raw) {
		this.raw = checkNotNull(raw);
		String[] parts = raw.split("\\|");
		this.sequenceNumber = Long.parseLong(parts[0]);
		this.type = EventType.byId(parts[1]);
		this.fromUser = parts.length <= 2 ? null : Integer.valueOf(parts[2]);
		this.toUser = parts.length <= 3 ? null : Integer.valueOf(parts[3]);
	}

	public long sequenceNumber() {
		return sequenceNumber;
	}

	public String raw() {
		return raw;
	}

	// TODO remove these
	EventType type() {
		return type;
	}

	int fromUser() {
		return fromUser;
	}

	int toUser() {
		return toUser;
	}

	@Override
	public String toString() {
		return raw;
	}

	public Set<Integer> recipients(UserRepository userRepository) {
		return type.recipientsFor(fromUser, toUser, userRepository);
	}
}
