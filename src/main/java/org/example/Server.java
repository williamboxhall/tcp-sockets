package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Server {
	private static final String DATA = "foo bar baz";

	public static void main(String args[]) {
		try {
			System.out.println("Running. Awaiting event source and client connection...");
			Socket eventSource = new ServerSocket(9090).accept();
			ServerSocket clientSocket = new ServerSocket(9099);
			Map<Integer, Socket> clients = new HashMap<>();
			List<String> eventQueue = new ArrayList<>();
			do {
				acceptNewClients(clientSocket, clients);
			} while (true);
			// TODO close all the sockets

		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}

	private static void acceptNewClients(ServerSocket clientSocket, Map<Integer, Socket> clients) throws IOException {

		Socket client = clientSocket.accept();
		BufferedReader in = new BufferedReader(new
				InputStreamReader(client.getInputStream()));
		System.out.print("Stored client: ");

		while (!in.ready()) {
		}
		int userId = Integer.parseInt(in.readLine());
		System.out.println(userId);
		clients.put(userId, client);
		in.close();
	}
}