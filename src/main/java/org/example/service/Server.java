package org.example.service;

import static org.example.infrastructure.Logger.LOG;
import static org.example.infrastructure.ShutdownHooks.closed;
import static org.example.infrastructure.ShutdownHooks.ensure;
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
import org.example.domain.UserFactory;
import org.example.domain.UserRepository;
import org.example.infrastructure.ConnectionFactory;
import org.example.infrastructure.Logger;
import org.example.infrastructure.Sockets;

public class Server {
	private final int eventSourcePort;
	private final int clientPort;
	private final UserRepository userRepository;
	private volatile boolean stopRequested = false;

	public Server(int eventSourcePort, int clientPort, boolean debug) {
		this.eventSourcePort = eventSourcePort;
		this.clientPort = clientPort;
		this.userRepository = new UserRepository(new UserFactory(), new ConnectionFactory());
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
				LOG.info("Running. Awaiting event source connection... ");
				final Socket eventSource = Sockets.accept(socketServerFor(eventSourcePort));

				LOG.info("SUCCESS");
				Map<Long, Event> eventQueue = new HashMap<>();
				Dispatcher dispatcher = new Dispatcher(userRepository);
				try {
					InputStream inputStream = eventSource.getInputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

					ensure(closed(eventSource));

					while (!stopRequested) {
						String raw = in.readLine();
						if (raw != null) {
							Event event = new Event(raw);
							eventQueue.put(event.sequenceNumber(), event);
						}
						dispatcher.drainQueuedEventsInOrder(eventQueue);
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
				ensure(closed(userRepository));
				ServerSocket server = socketServerFor(clientPort);
				while (!stopRequested) {
					Socket client = accept(server);
					userRepository.connect(integerFrom(client), client);
				}
			}
		};
	}
}