package org.example.infrastructure;

import org.example.domain.Event;

public interface Connection {
	void send(Event event);
}
