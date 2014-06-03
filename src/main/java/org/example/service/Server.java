package org.example.service;

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
import java.util.Set;

import org.example.domain.UserFactory;
import org.example.domain.UserRepository;
import org.example.infrastructure.ConnectionFactory;

class Server {

	private static final int EVENT_SOURCE_PORT = 9090;
	private static final int CLIENT_PORT = 9099;

	public static void main(String args[]) {
		try {
			System.out.print("Running. Awaiting event source connection... ");
			Socket eventSource = new ServerSocket(EVENT_SOURCE_PORT).accept();
			System.out.println("SUCCESS");

			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(CLIENT_PORT));
			serverSocketChannel.configureBlocking(false);

			UserRepository userRepository = new UserRepository(new UserFactory(), new ConnectionFactory());
			Map<Long, String> eventQueue = new HashMap<>();

			long lastDispatchedSeqNo = 0;

			// for each batch
			while (true) {
				acceptNewClients(serverSocketChannel, userRepository);
				enqueueNextBatchOfEvents(eventSource, eventQueue);
				lastDispatchedSeqNo = drainQueuedEventsInOrder(eventQueue, userRepository, lastDispatchedSeqNo);
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

	private static void enqueueNextBatchOfEvents(Socket eventSource, Map<Long, String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		if (in.ready()) {
			while (in.ready()) {
				String event = in.readLine();
				long seqNo = Long.parseLong(event.split("\\|")[0]);
				eventQueue.put(seqNo, event);
			}
		}
	}

	private static long drainQueuedEventsInOrder(Map<Long, String> eventQueue, UserRepository userRepository, long lastDispatchedSeqNo) throws IOException {
		if (!eventQueue.isEmpty()) {
			while (!eventQueue.isEmpty()) {
				lastDispatchedSeqNo = lastDispatchedSeqNo + 1;
				String event = eventQueue.get(lastDispatchedSeqNo);
				if (event == null) {
					throw new Error("No event found for " + lastDispatchedSeqNo);
				}
				dispatch(event, userRepository);
				eventQueue.remove(lastDispatchedSeqNo);
			}
		}
		return lastDispatchedSeqNo;
	}

	private static void dispatch(String event, UserRepository userRepository) throws IOException {
		String[] parts = event.split("\\|");
		String type = parts[1];
		Integer fromUserId = parts.length <= 2 ? null : Integer.valueOf(parts[2]);
		Integer toUserId = parts.length <= 3 ? null : Integer.valueOf(parts[3]);

		switch (type) {
			case "F":
				userRepository.get(toUserId).addFollower(fromUserId);
				writeStringToSocket(toUserId, event, userRepository);
				break;
			case "P":
				// TODO what is there is no following here for private messages?
				writeStringToSocket(toUserId, event, userRepository);
				break;
			case "U":
				userRepository.get(toUserId).removeFollower(fromUserId);
				break; // TODO follow up desired behavior when a message is sent to someone without following
			case "B":
				for (Integer clientId : userRepository.allUserIds()) {
					writeStringToSocket(clientId, event, userRepository);
				}
				break;
			case "S":
				Set<Integer> toUserIds = userRepository.get(fromUserId).getFollowers();
				for (int recipient : toUserIds) {
					writeStringToSocket(recipient, event, userRepository);
				}
				break;
			default:
				throw new Error("Unrecognised event type: " + type);
		}
	}

	private static void writeStringToSocket(int clientId, String event, UserRepository userRepository) throws IOException {
		userRepository.get(clientId).send(event);
	}
}