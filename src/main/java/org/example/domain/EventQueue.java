package org.example.domain;

import static org.example.infrastructure.Sockets.bufferedReaderFor;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class EventQueue {
	private final Map<Long, Event> eventQueue = new HashMap<>();
	private final BufferedReader bufferedReader;

	private EventQueue(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public static EventQueue eventQueueFor(Socket socket) {
		return new EventQueue(bufferedReaderFor(socket));
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void put(long sequenceNumber, Event event) {
		eventQueue.put(sequenceNumber, event);
	}

	public Map<Long, Event> getQueue() {
		return eventQueue;
	}
}
