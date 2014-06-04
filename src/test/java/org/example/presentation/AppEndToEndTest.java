package org.example.presentation;

import static org.example.presentation.App.CLIENT_PORT;
import static org.example.presentation.App.EVENT_SOURCE_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.example.domain.Event;
import org.example.infrastructure.SocketConnection;
import org.junit.Test;

public class AppEndToEndTest {
	@Test
	public void clientReceivesRelevantEvent() throws IOException, InterruptedException {
		App.main();

		Socket firstClient = new Socket("localhost", CLIENT_PORT);
		SocketConnection eventSource = new SocketConnection(new Socket("localhost", EVENT_SOURCE_PORT));

		new PrintWriter(firstClient.getOutputStream(), true).println("1");

		eventSource.send(new Event("1|F|1|2"));
		eventSource.send(new Event("2|F|2|1"));

		BufferedReader firstClientIn = new BufferedReader(new InputStreamReader(firstClient.getInputStream()));
		waitUntilReady(firstClientIn);
		assertThat(firstClientIn.readLine(), is("2|F|2|1"));

		eventSource.close();
		firstClient.close();
	}

	@Test
	public void clientAndEventSourceComingGoingAndReturning() {

	}

	@Test
	public void eventSequenceNumberGreatThanMaxInteger() {

	}

	@Test
	public void eventsArrivingOutOfOrder() {

	}

	private void waitUntilReady(BufferedReader reader) throws IOException {
		while (!reader.ready()) {
		}
	}
}