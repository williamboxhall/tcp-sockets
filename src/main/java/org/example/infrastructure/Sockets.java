package org.example.infrastructure;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Sockets {
	private Sockets() {
	}

	public static ServerSocket serverFor(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(port));
			return serverSocket;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Socket accept(ServerSocket serverSocket) {
		try {
			return serverSocket.accept();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void untilEmpty(Socket socket, Consumer<String> consumer) {
		BufferedReader bufferedReader = bufferedReaderFor(socket);
		String line = readLine(bufferedReader);
		while (line != null) {
			consumer.accept(line);
			line = readLine(bufferedReader);
		}
	}

	public static int integerFrom(Socket socket) {
		try {
			return Integer.parseInt(bufferedReaderFor(socket).readLine());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void println(String string, Socket socket) throws IOException {
		new PrintWriter(socket.getOutputStream(), true).println(string);
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
		}
	}

	private static BufferedReader bufferedReaderFor(Socket socket) {
		try {
			return new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readLine(BufferedReader bufferedReader) {
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
