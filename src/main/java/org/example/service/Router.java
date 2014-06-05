package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.closeQuietly;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Router implements Closeable {
	private final UserRepository userRepository;
	private final Map<Integer, Socket> registry;
	private long nextToDispatch = 1;

	public Router(UserRepository userRepository, Map<Integer, Socket> registry) {
		this.userRepository = userRepository;
		this.registry = registry;
	}

	void drainQueuedEventsInOrder(Map<Long, Event> eventQueue) {
		while (nextEventIsReady(eventQueue)) {
			Event event = eventQueue.get(nextToDispatch);
			for (int userId : event.type().recipientsFor(event, userRepository)) {
				notify(event, userId);
			}
			eventQueue.remove(nextToDispatch);
			nextToDispatch++;
		}
	}

	public void connect(int userId, Socket socket) {
		registry.put(userId, socket);
		LOG.info(format("User %s connected", userId));
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
			registry.remove(userId);
			LOG.debug(format("Dropped event %s to user %s", event, userId));
		}
	}

	private boolean nextEventIsReady(Map<Long, Event> eventQueue) {
		return eventQueue.containsKey(nextToDispatch);
	}

	@Override
	public void close() {
		for (Socket socket : registry.values()) {
			closeQuietly(socket);
		}
	}
}
