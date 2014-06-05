package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.Sockets.println;

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
		for (int recipient : event.updateAndReturnRecipients(userRepository)) {
			notify(event, recipient);
		}
	}

	private void notify(Event event, int recipient) {
		try {
			if (registered(recipient)) {
				send(event, recipient);
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

	private void send(Event event, int recipient) throws IOException {
		println(event.raw(), registry.get(recipient));
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
