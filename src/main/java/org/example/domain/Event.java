package org.example.domain;

public class Event {
	private final String raw;
	private final long sequenceNumber;
	private final EventType type;
	private final Integer fromUserId;
	private final Integer toUserId;

	public Event(String raw) {
		this.raw = raw;
		String[] parts = raw.split("\\|");
		this.sequenceNumber = Long.parseLong(parts[0]);
		this.type = EventType.byId(parts[1]);
		this.fromUserId = parts.length <= 2 ? null : Integer.valueOf(parts[2]);
		this.toUserId = parts.length <= 3 ? null : Integer.valueOf(parts[3]);
		// TODO better handling for malformed events
	}

	public long sequenceNumber() {
		return sequenceNumber;
	}

	public EventType type() {
		return type;
	}

	public int fromUserId() {
		return fromUserId;
	}

	public int toUserId() {
		return toUserId;
	}

	public String raw() {
		return raw;
	}

	@Override
	public String toString() {
		return raw;
	}
}
