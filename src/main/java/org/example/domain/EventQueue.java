package org.example.domain;

import static org.example.infrastructure.Sockets.bufferedReaderFor;
import static org.example.infrastructure.Sockets.readLine;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.example.infrastructure.Consumer;

public class EventQueue {
	private final Map<Long, Event> eventQueue = new HashMap<>();
	private final BufferedReader bufferedReader;
	private long nextSequenceNo = 1;

	private EventQueue(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public static EventQueue eventQueueFor(Socket socket) {
		return new EventQueue(bufferedReaderFor(socket));
	}

	public void forEach(Consumer<Event> consumer) {
		String raw = readLine(bufferedReader);
		while (raw != null) {
			notify(consumer, new Event(raw));
			raw = readLine(bufferedReader);
		}
	}

	private void notify(Consumer<Event> consumer, Event event) {
		eventQueue.put(event.sequenceNumber(), event);
		while (eventQueue.containsKey(nextSequenceNo)) {
			consumer.accept(event);
			eventQueue.remove(nextSequenceNo);
			nextSequenceNo++;
		}
	}
}
