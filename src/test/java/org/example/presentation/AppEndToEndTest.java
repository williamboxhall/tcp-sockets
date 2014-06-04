package org.example.presentation;

import static java.lang.String.valueOf;
import static org.example.presentation.App.CLIENT_PORT;
import static org.example.presentation.App.EVENT_SOURCE_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class AppEndToEndTest {
	private Socket eventSource;
	private Socket client;

	@BeforeClass
	public static void startServer() throws IOException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(false));
	}

	@AfterClass
	public static void stopServer() {
		// handled by shutdown hook in Server.java
	}

	@Before
	public void openSockets() throws IOException {
		client = new Socket("localhost", CLIENT_PORT);
		eventSource = new Socket("localhost", EVENT_SOURCE_PORT);
	}

	@After
	public void closeSockets() throws IOException {
		eventSource.close();
		client.close();
	}

	@Test
	public void clientReceivesRelevantEvents() throws IOException, InterruptedException {
		write(client, "1");

		write(eventSource, "1|F|2|1");
		write(eventSource, "2|F|3|4");
		write(eventSource, "3|F|1|2");
		write(eventSource, "4|F|3|1");

		assertThat(readLine(client), is("1|F|2|1"));
		assertThat(readLine(client), is("4|F|3|1"));
	}

	@Test
	@Ignore
	public void clientAndEventSourceComingGoingAndReturning() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void eventsArrivingOutOfOrder() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void sameEventSequenceNumberSeenTwice() {
		fail("Not yet implemented");
	}

	private void write(Socket socket, String message) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}

	private String readLine(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while (!reader.ready()) {
		}
		return reader.readLine();
	}
}