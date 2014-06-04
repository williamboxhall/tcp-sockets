package org.example.presentation;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import org.example.service.Server;

public class App {
	static final int EVENT_SOURCE_PORT = 9090;
	static final int CLIENT_PORT = 9099;

	// TODO provide commandline args
	public static void main(String... args) {
		int eventSourcePort = args.length > 0 ? parseInt(args[0]) : EVENT_SOURCE_PORT;
		int clientPort = args.length > 1 ? parseInt(args[1]) : CLIENT_PORT;
		boolean debug = args.length > 2 && parseBoolean(args[2]);
		long eventSeqNoOffset = args.length > 3 ? parseLong(args[3]) : 0;
		new Server(eventSourcePort, clientPort, debug, eventSeqNoOffset).start();
	}
}
