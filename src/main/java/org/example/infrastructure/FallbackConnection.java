package org.example.infrastructure;

import org.example.domain.Event;

public class FallbackConnection implements Connection {
	private final Connection primary;
	private final Connection secondary;
	private boolean failOver = false;

	public FallbackConnection(Connection primary, Connection secondary) {
		this.primary = primary;
		this.secondary = secondary;
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
				System.out.println("Disconnected"); // TODO replace all printlns with log
				secondary.send(event);
			}
		}
	}
}
