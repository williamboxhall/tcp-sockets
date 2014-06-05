package org.example.service;

import static java.lang.String.format;
import static org.example.domain.EventSequencer.sendTo;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.ShutdownHooks.ensureClosedOnExit;
import static org.example.infrastructure.Sockets.accept;
import static org.example.infrastructure.Sockets.integerFrom;
import static org.example.infrastructure.Sockets.serverFor;
import static org.example.infrastructure.Sockets.untilEmpty;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.example.domain.UserRepository;

public class Server {
	private final int eventSourcePort;
	private final int clientPort;
	private final Router router;

	public Server(int eventSourcePort, int clientPort, Map<Integer, Socket> registry) {
		this.eventSourcePort = eventSourcePort;
		this.clientPort = clientPort;
		this.router = new Router(new UserRepository(new UserRepository.UserFactory()), registry);
	}

	public void start() {
		start(clientsThread());
		start(eventThread());
		LOG.info("Ctrl-C to safely shut down");
	}

	private void start(Thread thread) {
		thread.start();
		LOG.info(format("%s thread started", thread.getName()));
	}

	private Thread eventThread() {
		return new Thread("events") {
			@Override
			public void run() {
				ServerSocket server = serverFor(eventSourcePort);
				while (true) {
					untilEmpty(ensureClosedOnExit(accept(server)), sendTo(router));
				}
			}
		};
	}

	private Thread clientsThread() {
		return new Thread("clients") {
			@Override
			public void run() {
				ServerSocket server = serverFor(clientPort);
				while (true) {
					Socket client = ensureClosedOnExit(accept(server));
					router.connect(integerFrom(client), client);
				}
			}
		};
	}
}