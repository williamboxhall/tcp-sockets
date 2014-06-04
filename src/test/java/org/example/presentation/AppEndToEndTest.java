package org.example.presentation;

import static java.lang.String.valueOf;
import static org.example.presentation.App.CLIENT_PORT;
import static org.example.presentation.App.EVENT_SOURCE_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
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
	private static Socket eventSource;
	private static Socket client;

	@BeforeClass
	public static void startServer() throws IOException {
		App.main(valueOf(EVENT_SOURCE_PORT), valueOf(CLIENT_PORT), valueOf(false));
		client = new Socket("localhost", CLIENT_PORT);
		eventSource = new Socket("localhost", EVENT_SOURCE_PORT);
		write(client, "1");
	}

	@AfterClass
	public static void stopServer() throws IOException {
		eventSource.close();
		client.close();
	}

	@Test
	public void allScenarios() throws IOException, InterruptedException {
		assertClientReceiverRelevantEvents();
		assertClientsMayDisconnectAndReconnect();
	}

	private void assertClientReceiverRelevantEvents() throws IOException {
		write(eventSource, "1|F|2|1");
		write(eventSource, "2|F|3|4");
		write(eventSource, "3|F|1|2");
		write(eventSource, "4|F|3|1");

		assertThat(readLine(client), is("1|F|2|1"));
		assertThat(readLine(client), is("4|F|3|1"));
	}

	private void assertClientsMayDisconnectAndReconnect() throws IOException, InterruptedException {
		write(eventSource, "5|B");
		assertThat(readLine(client), is("5|B"));

		client.close();
		write(eventSource, "6|B");
		client = new Socket("localhost", CLIENT_PORT);
		write(client, "1");

		write(eventSource, "7|F|3|4");
		write(eventSource, "8|U|3|4");
		write(eventSource, "9|B");
		assertThat(readLine(client), is("9|B"));
	}

	private static void write(Socket socket, String message) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}

	private static String readLine(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while (!reader.ready()) {
		}
		return reader.readLine();
	}
}