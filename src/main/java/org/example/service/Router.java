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

	@Override
	public void accept(Event event) {
		for (int recipient : event.updateAndReturnRecipients(userRepository)) {
			notify(event, recipient);
		}
	}

	private void notify(Event event, int recipient) {
		try {
			Socket socket = registry.get(recipient);
			if (socket != null) {
				new PrintWriter(socket.getOutputStream(), true).println(event.raw());
				LOG.debug(format("Sent event %s to user %s", event, recipient));
			} else {
				LOG.debug(format("Dropped event %s to user %s", event, recipient));
			}
		} catch (IOException e) {
			LOG.info(format("User %s disconnected", recipient));
			disconnect(recipient);
			LOG.debug(format("Dropped event %s to user %s", event, recipient));
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
