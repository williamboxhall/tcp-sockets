package org.example.domain;

import java.util.HashMap;
import java.util.Map;

import org.example.infrastructure.Consumer;

public class EventQueue implements Consumer<String> {
	private final Map<Long, Event> eventQueue = new HashMap<>();
	private final Consumer<Event> consumer;
	private long nextSequenceNo = 1;

	private EventQueue(Consumer<Event> consumer) {
		this.consumer = consumer;
	}

	public static EventQueue sendTo(Consumer<Event> consumer) {
		return new EventQueue(consumer);
	}

	@Override
	public void accept(String raw) {
		Event event = new Event(raw);
		eventQueue.put(event.sequenceNumber(), event);
		while (eventQueue.containsKey(nextSequenceNo)) {
			consumer.accept(event);
			eventQueue.remove(nextSequenceNo);
			nextSequenceNo++;
		}
	}
}
