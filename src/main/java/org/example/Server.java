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
import java.util.Iterator;
import java.util.Map;

class Server {
	public static void main(String args[]) {
		try {
			System.out.println("Running. Awaiting event source connection...");
			Socket eventSource = new ServerSocket(9090).accept();


			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9099));
			serverSocketChannel.configureBlocking(false);

			Map<Integer, SocketChannel> clients = new HashMap<>();
			Map<Integer, String> eventQueue = new HashMap<>();

			int lastDispatchedSeqNo = 0;

			// for each batch
			while (true) {
				SocketChannel clientSocketChannel = serverSocketChannel.accept();
				if (clientSocketChannel != null) {
					acceptNewClients(clientSocketChannel, clients);
				}
				enqueueEvents(eventSource, eventQueue);
				lastDispatchedSeqNo = processPrivateMessageEvents(eventQueue, clients, lastDispatchedSeqNo);
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

	private static void enqueueEvents(Socket eventSource, Map<Integer, String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		while (in.ready()) {
			String event = in.readLine();
			System.out.println("Event:" + event);
			int seqNo = Integer.parseInt(event.split("\\|")[0]);
			eventQueue.put(seqNo, event);
		}
	}

	// TODO currently doesn't work for out of order temporal failures (follow event came after message)
	private static int processPrivateMessageEvents(Map<Integer, String> eventQueue, Map<Integer, SocketChannel> clients, int lastDispatchedSeqNo) throws IOException {
		while (!eventQueue.isEmpty()) {
			lastDispatchedSeqNo = lastDispatchedSeqNo + 1;
			String event = eventQueue.get(lastDispatchedSeqNo);
			if (event == null) {
				System.out.println("no event found for index " + lastDispatchedSeqNo);
			}
			System.out.print(lastDispatchedSeqNo + ",");
			eventQueue.remove(lastDispatchedSeqNo);
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