package org.example.infrastructure;

import org.example.domain.Event;

public interface Connection {
	public static Connection BLACK_HOLE = new Connection() {
		@Override
		public void send(Event event) {
		}
	};

	void send(Event event);
}
