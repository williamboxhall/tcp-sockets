package org.example.service;

import static org.example.infrastructure.Logger.LOG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserFactory;
import org.example.domain.UserRepository;
import org.example.infrastructure.ConnectionFactory;

class Server {
	private static final int EVENT_SOURCE_PORT = 9090;
	private static final int CLIENT_PORT = 9099;

	public static void main(String args[]) {
		UserRepository userRepository = new UserRepository(new UserFactory(), new ConnectionFactory());
		startClientsThread(userRepository);
		LOG.info("Client thread started");
		startEventThread(userRepository);
		LOG.info("Event thread started");
	}

	private static void startEventThread(final UserRepository userRepository) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket eventSource = null;
				try {
					LOG.info("Running. Awaiting event source connection... ");
					eventSource = new ServerSocket(EVENT_SOURCE_PORT).accept();
					LOG.info("SUCCESS");
					Map<Long, Event> eventQueue = new HashMap<>();
					Dispatcher dispatcher = new Dispatcher(userRepository);
					while (true) {
						enqueueNextBatchOfEvents(eventSource, eventQueue);
						dispatcher.drainQueuedEventsInOrder(eventQueue);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (eventSource != null) {
							eventSource.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, "event thread").start();
	}

	private static void startClientsThread(final UserRepository userRepository) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(CLIENT_PORT);
					while (true) {
						acceptNewClients(serverSocket.accept(), userRepository);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					userRepository.disconnectAll();
				}
			}
		}, "client thread").start();
	}

	private static void acceptNewClients(Socket clientSocket, UserRepository userRepository) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		int userId = Integer.parseInt(in.readLine());
		userRepository.connect(userId, clientSocket);
	}

	private static void enqueueNextBatchOfEvents(Socket eventSource, Map<Long, Event> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		while (in.ready()) {
			Event event = new Event(in.readLine());
			eventQueue.put(event.sequenceNumber(), event);
		}
	}
}