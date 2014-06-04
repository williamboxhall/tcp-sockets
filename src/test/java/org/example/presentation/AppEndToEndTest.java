package org.example.presentation;

import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppEndToEndTest {
	private static final int EVENT_SOURCE_PORT = 1234;
	private static final int CLIENT_PORT = 5678;
	private static Socket eventSource;
	private static Socket client;
	private static int seqNo = 0;

	@BeforeClass
	public static void startServer() throws IOException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT));
		eventSource = new Socket("localhost", EVENT_SOURCE_PORT);
		client = new Socket("localhost", CLIENT_PORT);
		write(client, "1");
		waitForClientToFinishConnection();
	}

	@AfterClass
	public static void stopServer() throws IOException {
		eventSource.close();
		client.close();
		// App sockets closed by shutdown hook
	}

	@Test
	public void clientReceivesOnlyRelevantEventsInSequentialOrder() throws IOException, InterruptedException {
		write(eventSource, (seqNo + 5) + "|F|3|1");
		write(eventSource, (seqNo + 2) + "|F|4|5");
		write(eventSource, (seqNo + 3) + "|F|3|4");
		write(eventSource, (seqNo + 4) + "|F|1|2");
		write(eventSource, (seqNo + 1) + "|F|2|1");

		assertThat(readLine(client), is((seqNo + 1) + "|F|2|1"));
		assertThat(readLine(client), is((seqNo + 5) + "|F|3|1"));
	}

	private static String readLine(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return reader.readLine();
	}

	private static void waitForClientToFinishConnection() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		while (!reader.ready()) {
			writeEvent("P|99|1");
		}
		while (reader.ready()) {
			assertThat(reader.readLine(), containsString("P|99|1"));
		}
	}

	private static void writeEvent(String eventSubstring) throws IOException {
		write(eventSource, valueOf(++seqNo) + "|" + eventSubstring);
	}

	private static void write(Socket socket, String message) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}
}