package org.example.service;

import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.ShutdownHooks.closed;
import static org.example.infrastructure.ShutdownHooks.ensureOnExit;
import static org.example.infrastructure.Sockets.accept;
import static org.example.infrastructure.Sockets.integerFrom;
import static org.example.infrastructure.Sockets.socketServerFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;
import org.example.infrastructure.Logger;
import org.example.infrastructure.Sockets;

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
		clientsThread().start();
		LOG.info("Client thread started");
		eventThread().start();
		LOG.info("Event thread started");
		LOG.info("Ctrl-C to shut down");
	}

	private Thread eventThread() {
		return new Thread("events") {
			@Override
			public void run() {
				try {
					Map<Long, Event> eventQueue = new HashMap<>();

					while (true) {
						final Socket eventSource = Sockets.accept(socketServerFor(eventSourcePort));
						InputStream inputStream = eventSource.getInputStream();
						BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

						ensureOnExit(closed(eventSource));
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
				ensureOnExit(closed(router));
				ServerSocket server = socketServerFor(clientPort);
				while (true) {
					Socket client = accept(server);
					router.connect(integerFrom(client), client);
				}
			}
		};
	}
}