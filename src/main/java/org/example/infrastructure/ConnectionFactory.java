package org.example.infrastructure;

import static org.example.infrastructure.Connection.BLACK_HOLE;

import java.nio.channels.SocketChannel;

public class ConnectionFactory {
	public Connection createFor(SocketChannel socketChannel) {
		return new FallbackConnection(new SocketConnection(socketChannel), BLACK_HOLE);
	}
}
