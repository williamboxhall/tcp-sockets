package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

class Server {
	public static void main(String args[]) {
		try {
			System.out.println("Running. Awaiting event source connection...");
			Socket eventSource = new ServerSocket(9090).accept();


			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9099));
			serverSocketChannel.configureBlocking(false);

			Map<Integer, SocketChannel> clients = new HashMap<>();
			Queue<String> eventQueue = new LinkedList<>();

			while (true) {
				SocketChannel clientSocketChannel =
						serverSocketChannel.accept();
				if (clientSocketChannel != null) {
					acceptNewClients(clientSocketChannel, clients);
				}
				enqueueEvents(eventSource, eventQueue);
				processPrivateMessageEvents(eventQueue, clients);

			}
			// TODO close all the sockets
		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}

	private static void acceptNewClients(SocketChannel clientSocketChannel, Map<Integer, SocketChannel> clients) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocketChannel.socket().getInputStream()));
		int userId = Integer.parseInt(in.readLine());
		System.out.println("User:" + userId);
		clients.put(userId, clientSocketChannel);
		in.close();
	}

	private static void enqueueEvents(Socket eventSource, Queue<String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		while (in.ready()) {
			String event = in.readLine();
			System.out.println("Event:" + event);
			eventQueue.add(event);
		}
	}

	// TODO currently doesn't work for out of order temporal failures (follow event came after message)
	private static void processPrivateMessageEvents(Queue<String> eventQueue, Map<Integer, SocketChannel> clients) throws IOException {
		String event = eventQueue.poll();
		while (event != null) {
			String[] parts = event.split("\\|");
			String messageType = parts[1];


			if (false && "P".equals(messageType)) {
				int toUserId = Integer.parseInt(parts[4]);
				String messagePayload = parts[0];

				// TODO may fail
				SocketChannel client = clients.get(toUserId);

				ByteBuffer buf = ByteBuffer.allocate(48);
				buf.clear();
				buf.put(event.getBytes());

				System.out.println(String.format("Sending user %d private message %s", toUserId, messagePayload));
				client.write(buf);
			}

			event = eventQueue.poll();
		}

	}
}