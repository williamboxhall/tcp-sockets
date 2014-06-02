package org.example;

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

class Server {
	public static void main(String args[]) {
		try {
			System.out.print("Running. Awaiting event source connection... ");
			Socket eventSource = new ServerSocket(9090).accept();
			System.out.println("SUCCESS");

			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9099));
			serverSocketChannel.configureBlocking(false);

			Map<Integer, SocketChannel> clients = new HashMap<>();
			Map<Long, String> eventQueue = new HashMap<>();

			long lastDispatchedSeqNo = 0;

			// for each batch
			while (true) {
				acceptNewClients(serverSocketChannel, clients);
				// TODO what about disconnecting clients?
				enqueueNextBatchOfEvents(eventSource, eventQueue);
				lastDispatchedSeqNo = drainQueuedEventsInOrder(eventQueue, clients, lastDispatchedSeqNo);
			}
			// TODO close all the sockets
		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}

	private static void acceptNewClients(ServerSocketChannel serverSocketChannel, Map<Integer, SocketChannel> clients) throws IOException {
		SocketChannel clientSocketChannel = serverSocketChannel.accept();
		if (clientSocketChannel != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocketChannel.socket().getInputStream()));
			int userId = Integer.parseInt(in.readLine());
			System.out.println("User:" + userId);
			clients.put(userId, clientSocketChannel);
			in.close();
		}
	}

	private static void enqueueNextBatchOfEvents(Socket eventSource, Map<Long, String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		if (in.ready()) {
			System.out.print("Events:");
			while (in.ready()) {
				String event = in.readLine();
				long seqNo = Long.parseLong(event.split("\\|")[0]);
				System.out.print(event + ",");
				eventQueue.put(seqNo, event);
			}
			System.out.println();
		}
	}

	private static long drainQueuedEventsInOrder(Map<Long, String> eventQueue, Map<Integer, SocketChannel> clients, long lastDispatchedSeqNo) throws IOException {
		if (!eventQueue.isEmpty()) {
			System.out.print("Dispatching:");
			while (!eventQueue.isEmpty()) {
				lastDispatchedSeqNo = lastDispatchedSeqNo + 1;
				String event = eventQueue.get(lastDispatchedSeqNo);
				if (event == null) {
					throw new Error("No event found for " + lastDispatchedSeqNo);
				}
				System.out.print(lastDispatchedSeqNo + ",");
				eventQueue.remove(lastDispatchedSeqNo);
			}
			System.out.println();
		}
		return lastDispatchedSeqNo;

//		String event = eventQueue.poll();
//		while (event != null) {
//			String[] parts = event.split("\\|");
//			String messageType = parts[1];
//
//
//			if (false && "P".equals(messageType)) {
//				int toUserId = Integer.parseInt(parts[4]);
//				String messagePayload = parts[0];
//
//				// TODO may fail
//				SocketChannel client = clients.get(toUserId);
//
//				ByteBuffer buf = ByteBuffer.allocate(48);
//				buf.clear();
//				buf.put(event.getBytes());
//
//				System.out.println(String.format("Sending user %d private message %s", toUserId, messagePayload));
//				client.write(buf);
//			}
//
//			event = eventQueue.poll();
//		}

	}
}