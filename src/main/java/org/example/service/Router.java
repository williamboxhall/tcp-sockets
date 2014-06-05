package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;
import org.example.infrastructure.Consumer;

public class Router implements Consumer<Event> {
	private final UserRepository userRepository;
	private final Map<Integer, Socket> registry;

	public Router(UserRepository userRepository, Map<Integer, Socket> registry) {
		this.userRepository = userRepository;
		this.registry = registry;
	}

	public void accept(Event event) {
		for (int userId : event.type().recipientsFor(event, userRepository)) {
			notify(event, userId);
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
}
