package org.example.presentation;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.example.infrastructure.Logger.DEBUG;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.infrastructure.Logger;
import org.example.service.Server;

public class App {
	static final Map<Integer, Socket> REGISTRY = new ConcurrentHashMap<>();
	private static final int EVENT_SOURCE_PORT = 9090;
	private static final int CLIENT_PORT = 9099;

	public static void main(String... args) {
		try {
			int eventSourcePort = args.length > 0 ? parseInt(args[0]) : EVENT_SOURCE_PORT;
			int clientPort = args.length > 1 ? parseInt(args[1]) : CLIENT_PORT;
			Logger.DEBUG = args.length > 2 ? parseBoolean(args[2]) : DEBUG;
			new Server(eventSourcePort, clientPort, REGISTRY).start();
		} catch (NumberFormatException e) {
			System.out.println(format("Invalid argument: %s", args));
			System.out.println("arg usage: ... [event source port (default 9090)] [client port (default 9099)] [debug enabled (default false)]");
			System.out.println("example usage: java -jar app.jar 1234 5678 true");
		}
	}
}
