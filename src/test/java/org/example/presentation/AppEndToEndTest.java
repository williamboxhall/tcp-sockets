package org.example.presentation;

import static java.lang.String.valueOf;
import static org.example.presentation.App.CLIENT_PORT;
import static org.example.presentation.App.EVENT_SOURCE_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppEndToEndTest {
	private static Socket eventSource;
	private static Socket client;
	private static int sequenceNumber = 1;

	@BeforeClass
	public static void startServer() throws IOException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(false));
		client = new Socket("localhost", CLIENT_PORT);
		eventSource = new Socket("localhost", EVENT_SOURCE_PORT);
		write(client, "1");
		waitForClientToFinishConnection(1);
	}

	@AfterClass
	public static void stopServer() throws IOException {
		eventSource.close();
		client.close();
		// App sockets closed by shutdown hook
	}

	@Test
	public void clientReceivesOnlyRelevantEvents() throws IOException, InterruptedException {
		writeEvent("F|2|1");
		writeEvent("F|3|4");
		writeEvent("F|1|2");
		writeEvent("F|3|1");

		assertThat(readLine(client), containsString("F|2|1"));
		assertThat(readLine(client), containsString("F|3|1"));
	}

	private static String readLine(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return reader.readLine();
	}

	private static void waitForClientToFinishConnection(int id) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		while (!reader.ready()) {
			writeEvent("P|99|" + id);
		}
		while (reader.ready()) {
			assertThat(reader.readLine(), containsString("P|99|" + id));
		}
	}

	private static void writeEvent(String eventSubstring) throws IOException {
		write(eventSource, valueOf(sequenceNumber++) + "|" + eventSubstring);
	}

	private static void write(Socket socket, String message) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}
}