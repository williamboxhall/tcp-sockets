package org.example.service;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.ShutdownHooks.ensureClosedOnExit;
import static org.example.infrastructure.Sockets.accept;
import static org.example.infrastructure.Sockets.bufferedReaderFor;
import static org.example.infrastructure.Sockets.integerFrom;
import static org.example.infrastructure.Sockets.socketServerFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;
import org.example.infrastructure.Logger;

public class Server {
	private final int eventSourcePort;
	private final int clientPort;
	private final Router router;

	public Server(int eventSourcePort, int clientPort, boolean debug, Map<Integer, Socket> registry) {
		this.eventSourcePort = eventSourcePort;
		this.clientPort = clientPort;
		this.router = new Router(new UserRepository(new UserRepository.UserFactory()), registry);
		Logger.DEBUG = debug;
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
				try {
					Map<Long, Event> eventQueue = new HashMap<>();
					ServerSocket server = socketServerFor(eventSourcePort);
					while (true) {
						BufferedReader in = bufferedReaderFor(ensureClosedOnExit(accept(server)));
						String raw = in.readLine();
						while (raw != null) {
							Event event = new Event(raw);
							eventQueue.put(event.sequenceNumber(), event);
							router.drainQueuedEventsInOrder(eventQueue);
							raw = in.readLine();
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private Thread clientsThread() {
		return new Thread("clients") {
			@Override
			public void run() {
				ServerSocket server = socketServerFor(clientPort);
				while (true) {
					Socket client = ensureClosedOnExit(accept(server));
					router.connect(integerFrom(client), client);
				}
			}
		};
	}
}