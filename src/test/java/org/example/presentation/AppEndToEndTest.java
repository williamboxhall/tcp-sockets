package org.example.presentation;

import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppEndToEndTest {
	private static final int EVENT_SOURCE_PORT = 1234;
	private static final int CLIENT_PORT = 5678;
	private Socket eventSource;
	private Socket firstClient;
	private Socket secondClient;

	@BeforeClass
	public static void startServer() throws IOException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(true));
	}

	@After
	public void stopServer() throws IOException {
		eventSource.close();
		firstClient.close();
		secondClient.close();
		// App sockets closed by shutdown hook
	}

	@Test
	public void clientReceivesOnlyRelevantEventsInSequentialOrder() throws IOException {
		eventSource = new Socket("localhost", EVENT_SOURCE_PORT);

		firstClient = new Socket("localhost", CLIENT_PORT);
		write(firstClient, "1");
		waitForClientToFinishConnection(1);

		write(eventSource, "5|F|3|1");
		write(eventSource, "2|F|4|5");
		write(eventSource, "3|F|3|4");
		write(eventSource, "4|F|1|2");
		write(eventSource, "1|F|2|1");
		assertThat(readLine(firstClient), is("1|F|2|1"));
		assertThat(readLine(firstClient), is("5|F|3|1"));

		secondClient = new Socket("localhost", CLIENT_PORT);
		write(secondClient, "2");
		waitForClientToFinishConnection(2);
		write(eventSource, "6|F|7|2");

		assertThat(readLine(secondClient), is("6|F|7|2"));
	}

	private static String readLine(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return reader.readLine();
	}

	@SuppressWarnings("StatementWithEmptyBody")
	private static void waitForClientToFinishConnection(int id) {
		while (!App.REGISTRY.containsKey(id)) {
		}
	}

	private static void write(Socket socket, String message) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}
}