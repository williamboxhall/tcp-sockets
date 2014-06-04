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

import org.example.domain.Event;
import org.example.infrastructure.SocketConnection;
import org.junit.Ignore;
import org.junit.Test;

public class AppEndToEndTest {
	@Test
	public void clientReceivesRelevantEvent() throws IOException, InterruptedException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(false), valueOf(0));

		Socket client = new Socket("localhost", CLIENT_PORT);
		SocketConnection eventSource = new SocketConnection(new Socket("localhost", EVENT_SOURCE_PORT));

		new PrintWriter(client.getOutputStream(), true).println("1");

		eventSource.send(new Event("1|F|2|1"));
		eventSource.send(new Event("2|F|3|4"));
		eventSource.send(new Event("3|F|1|2"));
		eventSource.send(new Event("4|F|3|1"));

		BufferedReader firstClientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
		waitUntilReady(firstClientIn);
		assertThat(firstClientIn.readLine(), is("1|F|2|1"));
		assertThat(firstClientIn.readLine(), is("4|F|3|1"));

		eventSource.close();
		client.close();
	}

	@Test
	@Ignore
	public void clientAndEventSourceComingGoingAndReturning() {
		fail("Not yet implemented");
	}

	@Test
	public void eventSequenceNumberGreaterThanMaxInteger() throws IOException {
		long largeSequenceNumber = Integer.MAX_VALUE - 1;
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(false), valueOf(largeSequenceNumber - 1));

		Socket client = new Socket("localhost", CLIENT_PORT);
		SocketConnection eventSource = new SocketConnection(new Socket("localhost", EVENT_SOURCE_PORT));

		new PrintWriter(client.getOutputStream(), true).println("1");

		BufferedReader firstClientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

		Event first = new Event(largeSequenceNumber + "|B");
		Event second = new Event((largeSequenceNumber + 1) + "|B");
		Event third = new Event((largeSequenceNumber + 2) + "|B");
		Event fourth = new Event((largeSequenceNumber + 3) + "|B");
		eventSource.send(first);
		eventSource.send(second);
		eventSource.send(second);
		eventSource.send(third);
		eventSource.send(fourth);

		waitUntilReady(firstClientIn);
		assertThat(firstClientIn.readLine(), is(first.raw()));
		assertThat(firstClientIn.readLine(), is(second.raw()));
		assertThat(firstClientIn.readLine(), is(third.raw()));
		assertThat(firstClientIn.readLine(), is(fourth.raw()));


		eventSource.close();
		client.close();
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

	private void waitUntilReady(BufferedReader reader) throws IOException {
		while (!reader.ready()) {
		}
	}
}