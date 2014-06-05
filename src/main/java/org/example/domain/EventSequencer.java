package org.example.domain;

import java.util.HashMap;
import java.util.Map;

import org.example.infrastructure.Consumer;

public class EventSequencer implements Consumer<String> {
	private final Map<Long, Event> backlog = new HashMap<>();
	private final Consumer<Event> consumer;
	private long next = 1;

	private EventSequencer(Consumer<Event> consumer) {
		this.consumer = consumer;
	}

	public static EventSequencer sendTo(Consumer<Event> consumer) {
		return new EventSequencer(consumer);
	}

	@Override
	public void accept(String raw) {
		Event event = new Event(raw); // TODO handling for bad event
		backlog.put(event.sequenceNumber(), event);
		while (backlog.containsKey(next)) {
			consumer.accept(backlog.remove(next++));
		}
	}
}
