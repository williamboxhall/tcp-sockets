package org.example.infrastructure;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Sockets {
	private Sockets() {
	}

	public static ServerSocket socketServerFor(int port) {
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

	public static int integerFrom(Socket socket) {
		try {
			return Integer.parseInt(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
		}
	}
}
