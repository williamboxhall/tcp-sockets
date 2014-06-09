package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.writeLine;

import java.io.IOException;
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
		for (int recipient : event.updateAndReturnRecipients(registry.keySet(), userRepository)) {
			notify(recipient, event);
		}
	}

	private void notify(int recipient, Event event) {
		try {
			if (registered(recipient)) {
				send(recipient, event);
				return;
			}
		} catch (IOException e) {
			disconnect(recipient);
		}
		LOG.debug(format("Dropped event %s to user %s", event, recipient));
	}

	private boolean registered(int recipient) {
		return registry.containsKey(recipient);
	}

	private void send(int recipient, Event event) throws IOException {
		writeLine(event.raw(), registry.get(recipient));
		LOG.debug(format("Sent event %s to user %s", event, recipient));
	}

	public void connect(int user, Socket socket) {
		registry.put(user, socket);
		LOG.info(format("User %s connected", user));
	}

	private void disconnect(int user) {
		registry.remove(user);
		LOG.info(format("User %s disconnected", user));
	}
}
