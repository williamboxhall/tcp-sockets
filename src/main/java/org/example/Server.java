package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
			Map<Long, String> eventQueue = new HashMap<>(); // TODO ask whether long is large enough

			long lastDispatchedSeqNo = 0;

			// for each batch
			Map<Integer, Set<Integer>> followerToFollowables = new HashMap<>();
			while (true) {
				acceptNewClients(serverSocketChannel, clients);
				// TODO what about disconnecting clients?
				enqueueNextBatchOfEvents(eventSource, eventQueue);
				lastDispatchedSeqNo = drainQueuedEventsInOrder(eventQueue, clients, lastDispatchedSeqNo, followerToFollowables);
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
			//System.out.println("User:" + userId);
			clients.put(userId, clientSocketChannel);
		}
	}

	private static void enqueueNextBatchOfEvents(Socket eventSource, Map<Long, String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		if (in.ready()) {
			//System.out.print("Events:");
			while (in.ready()) {
				String event = in.readLine();
				long seqNo = Long.parseLong(event.split("\\|")[0]);
				//System.out.print(event + ",");
				eventQueue.put(seqNo, event);
			}
			//System.out.println();
		}
	}

	private static long drainQueuedEventsInOrder(Map<Long, String> eventQueue, Map<Integer, SocketChannel> clients, long lastDispatchedSeqNo, Map<Integer, Set<Integer>> followerToFollowables) throws IOException {
		if (!eventQueue.isEmpty()) {
			//System.out.print("Dispatching:");
			while (!eventQueue.isEmpty()) {
				lastDispatchedSeqNo = lastDispatchedSeqNo + 1;
				String event = eventQueue.get(lastDispatchedSeqNo);
				if (event == null) {
					throw new Error("No event found for " + lastDispatchedSeqNo);
				}
				//System.out.print(lastDispatchedSeqNo + ",");
				dispatch(event, clients, followerToFollowables, lastDispatchedSeqNo);
				eventQueue.remove(lastDispatchedSeqNo);
			}
			//System.out.println();
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

	private static void dispatch(String event, Map<Integer, SocketChannel> clients, Map<Integer, Set<Integer>> followedToFollowers, long lastDispatchedSeqNo) throws IOException {
		String[] parts = event.split("\\|");
		String type = parts[1];
		Integer fromUserId = parts.length <= 2 ? null : Integer.valueOf(parts[2]);
		Integer toUserId = parts.length <= 3 ? null : Integer.valueOf(parts[3]);

		switch (type) {
			case "F":
				if (!followedToFollowers.containsKey(toUserId)) {
					Set<Integer> followers = new HashSet<>();
					followers.add(fromUserId);
					followedToFollowers.put(toUserId, followers);
				} else {
					followedToFollowers.get(toUserId).add(fromUserId);
				}
				writeStringToSocket(toUserId, event, clients);
				break;
			case "P":
				// TODO what is there is no following here for private messages?
				writeStringToSocket(toUserId, event, clients);
				break;
			case "U":
				followedToFollowers.get(toUserId).remove(fromUserId);
				break; // TODO follow up desired behavior when a message is sent to someone without following
			case "B":
				for (Integer clientId : clients.keySet()) {
					writeStringToSocket(clientId, event, clients);
				}
				break;
			case "S":
				Set<Integer> toUserIds = followedToFollowers.get(fromUserId);
				if (toUserIds != null) {
					for (int recipient : toUserIds) {
						writeStringToSocket(recipient, event, clients);
					}
				}
				break;
			default:
				throw new Error("Unrecognised event type: " + type);
		}
	}

	private static void writeStringToSocket(Integer clientId, String event, Map<Integer, SocketChannel> clients) throws IOException {
		if (clientId != null) {
			SocketChannel client = clients.get(clientId);
			if (client != null && client.isConnected()) {
				PrintWriter out = new PrintWriter(client.socket().getOutputStream(), true);
				out.println(event);
				//System.out.println("dispatched: " + event + "  to client " + clientId);
			} else {
				//System.out.println("Client not around to recieve message: " + clientId);
				//System.out.println("Dropping event: " + event + " to user " + clientId);
				clients.remove(clientId);
			}
		} else {
			//System.out.println("Client not registered: " + clientId);
		}
	}
}