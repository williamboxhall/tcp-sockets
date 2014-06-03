package org.example.service;

import static org.example.infrastructure.Logger.LOG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
		try {
			LOG.info("Running. Awaiting event source connection... ");
			Socket eventSource = new ServerSocket(EVENT_SOURCE_PORT).accept();
			LOG.info("SUCCESS");

			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(CLIENT_PORT));
			serverSocketChannel.configureBlocking(false);

			UserRepository userRepository = new UserRepository(new UserFactory(), new ConnectionFactory());
			Map<Long, Event> eventQueue = new HashMap<>();
			Dispatcher dispatcher = new Dispatcher(userRepository);

			// for each batch
			while (true) {
				acceptNewClients(serverSocketChannel, userRepository);
				enqueueNextBatchOfEvents(eventSource, eventQueue);
				dispatcher.drainQueuedEventsInOrder(eventQueue);
			}
			// TODO close all the sockets
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void acceptNewClients(ServerSocketChannel serverSocketChannel, UserRepository userRepository) throws IOException {
		SocketChannel clientSocketChannel = serverSocketChannel.accept();
		if (clientSocketChannel != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocketChannel.socket().getInputStream()));
			int userId = Integer.parseInt(in.readLine());
			userRepository.connect(userId, clientSocketChannel);
		}
	}

	private static void enqueueNextBatchOfEvents(Socket eventSource, Map<Long, Event> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		while (in.ready()) {
			Event event = new Event(in.readLine());
			eventQueue.put(event.sequenceNumber(), event);
		}
	}
}