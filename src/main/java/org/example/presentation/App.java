package org.example.presentation;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.service.Server;

public class App {
	static final int EVENT_SOURCE_PORT = 9090;
	static final int CLIENT_PORT = 9099;
	static final Map<Integer, Socket> REGISTRY = new ConcurrentHashMap<>();

	public static void main(String... args) {
		int eventSourcePort = args.length > 0 ? parseInt(args[0]) : EVENT_SOURCE_PORT;
		int clientPort = args.length > 1 ? parseInt(args[1]) : CLIENT_PORT;
		boolean debug = args.length > 2 && parseBoolean(args[2]);
		new Server(eventSourcePort, clientPort, debug, REGISTRY).start();
	}
}
