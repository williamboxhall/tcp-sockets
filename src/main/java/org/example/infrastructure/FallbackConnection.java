package org.example.infrastructure;

import static java.lang.String.format;
import static org.example.infrastructure.Logger.LOG;

import org.example.domain.Event;

public class FallbackConnection implements Connection {
	private final Connection primary;
	private final Connection secondary;
	private final String name;
	private boolean failOver = false;

	public FallbackConnection(Connection primary, Connection secondary, String name) {
		this.primary = primary;
		this.secondary = secondary;
		this.name = name;
	}

	@Override
	public void send(Event event) {
		if (failOver) {
			secondary.send(event);
		} else {
			try {
				primary.send(event);
			} catch (RuntimeException e) {
				failOver = true;
				LOG.info(format("User %s disconnected", name));
				secondary.send(event);
			}
		}
	}
}
