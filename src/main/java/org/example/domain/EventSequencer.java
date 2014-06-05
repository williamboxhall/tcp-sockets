package org.example.domain;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;

import java.util.HashMap;
import java.util.Map;

import org.example.infrastructure.Consumer;

public class EventSequencer implements Consumer<String> {
	private final Map<Long, Event> backlog = new HashMap<>();
	private final Consumer<Event> consumer;
	private long next;

	EventSequencer(Consumer<Event> consumer, long offset) {
		this.consumer = consumer;
		this.next = offset + 1;
	}

	public static EventSequencer sendTo(Consumer<Event> consumer) {
		return new EventSequencer(consumer, 0);
	}

	@Override
	public void accept(String raw) {
		try {
			Event event = new Event(raw);
			backlog.put(event.sequenceNumber(), event);
			while (backlog.containsKey(next)) {
				consumer.accept(backlog.remove(next++));
			}
		} catch (IllegalArgumentException e) {
			LOG.error(format("Dropping malformed event '%s'", raw));
		}
	}
}
