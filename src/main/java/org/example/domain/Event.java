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
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public EventType getType() {
		return type;
	}

	public int getFromUserId() {
		return fromUserId;
	}

	public int getToUserId() {
		return toUserId;
	}

	public String raw() {
		return raw;
	}
}
