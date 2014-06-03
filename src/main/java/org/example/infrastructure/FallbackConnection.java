package org.example.infrastructure;

public class FallbackConnection implements Connection {
	private final Connection primary;
	private final Connection secondary;
	private boolean failOver = false;

	public FallbackConnection(Connection primary, Connection secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	@Override
	public void send(String message) {
		if (failOver) {
			secondary.send(message);
		} else {
			try {
				primary.send(message);
			} catch (RuntimeException e) {
				failOver = true;
				System.out.println("Disconnected"); // TODO replace all printlns with log
				secondary.send(message);
			}
		}
	}
}
