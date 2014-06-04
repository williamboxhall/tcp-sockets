package org.example.presentation;

import org.example.infrastructure.Logger;
import org.example.service.Server;

public class App {
	static final int EVENT_SOURCE_PORT = 9090;
	static final int CLIENT_PORT = 9099;

	// TODO provide commandline args
	public static void main(String... args) {
		Logger.DEBUG = true;
		new Server(EVENT_SOURCE_PORT, CLIENT_PORT).start();
	}
}
