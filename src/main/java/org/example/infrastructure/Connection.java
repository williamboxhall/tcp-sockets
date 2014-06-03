package org.example.infrastructure;

public interface Connection {
	public static Connection BLACK_HOLE = new Connection() {
		@Override
		public void send(String message) {
		}
	};

	void send(String message);
}
