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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

class Server {
	private static final String DATA = "foo bar baz";

	public static void main(String args[]) {
		try {
			System.out.println("Running. Awaiting event source connection...");
			Socket eventSource = new ServerSocket(9090).accept();


			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9099));
			serverSocketChannel.configureBlocking(false);

			Map<Integer, Socket> clients = new HashMap<>();
			Queue<String> eventQueue = new LinkedList<>();

			while (true) {
				SocketChannel clientSocketChannel =
						serverSocketChannel.accept();
				if (clientSocketChannel != null) {
					acceptNewClients(clientSocketChannel.socket(), clients);
				}
				enqueueEvents(eventSource, eventQueue);
			}
			// TODO close all the sockets
		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}

	private static void acceptNewClients(Socket client, Map<Integer, Socket> clients) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		int userId = Integer.parseInt(in.readLine());
		System.out.println("User:" + userId);
		clients.put(userId, client);
		in.close();
	}

	private static void enqueueEvents(Socket eventSource, Queue<String> eventQueue) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(eventSource.getInputStream()));
		if (in.ready()) {
			String event = in.readLine();
			System.out.println("Event:" + event);
			eventQueue.add(event);
		}
	}
}