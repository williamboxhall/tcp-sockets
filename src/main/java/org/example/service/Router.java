package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.readLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.EventQueue;
import org.example.domain.UserRepository;

public class Router {
	private final UserRepository userRepository;
	private final Map<Integer, Socket> registry;
	private long nextToDispatch = 1;

	public Router(UserRepository userRepository, Map<Integer, Socket> registry) {
		this.userRepository = userRepository;
		this.registry = registry;
	}

	void drainInOrder(EventQueue eventQueue) {
		BufferedReader in = eventQueue.getBufferedReader();
		for (String raw = readLine(in); raw != null; raw = readLine(in)) {
			Event event = new Event(raw);
			eventQueue.put(event.sequenceNumber(), event);
			drainInOrder(eventQueue.getQueue());
		}
	}

	private void drainInOrder(Map<Long, Event> eventQueue) {
		while (nextEventIsReady(eventQueue)) {
			Event event = eventQueue.get(nextToDispatch);
			for (int userId : event.type().recipientsFor(event, userRepository)) {
				notify(event, userId);
			}
			eventQueue.remove(nextToDispatch);
			nextToDispatch++;
		}
	}

	private void notify(Event event, int userId) {
		try {
			Socket socket = registry.get(userId);
			if (socket != null) {
				new PrintWriter(socket.getOutputStream(), true).println(event.raw());
				LOG.debug(format("Sent event %s to user %s", event, userId));
			} else {
				LOG.debug(format("Dropped event %s to user %s", event, userId));
			}
		} catch (IOException e) {
			LOG.info(format("User %s disconnected", userId));
			disconnect(userId);
			LOG.debug(format("Dropped event %s to user %s", event, userId));
		}
	}

	public void connect(int userId, Socket socket) {
		registry.put(userId, socket);
		LOG.info(format("User %s connected", userId));
	}

	private void disconnect(int userId) {
		registry.remove(userId);
	}

	private boolean nextEventIsReady(Map<Long, Event> eventQueue) {
		return eventQueue.containsKey(nextToDispatch);
	}
}
