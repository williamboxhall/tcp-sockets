package org.example.domain;

import static org.example.infrastructure.Sockets.untilEmpty;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.example.infrastructure.Consumer;

public class EventQueue {
	private final Map<Long, Event> eventQueue = new HashMap<>();
	private final Socket socket;
	private long nextSequenceNo = 1;

	private EventQueue(Socket socket) {
		this.socket = socket;
	}

	public static EventQueue eventQueueFor(Socket socket) {
		return new EventQueue(socket);
	}

	public void forEach(Consumer<Event> consumer) {
		untilEmpty(socket, notify(consumer));
	}

	private Consumer<String> notify(final Consumer<Event> consumer) {
		return new Consumer<String>() {
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
		};
	}
}
