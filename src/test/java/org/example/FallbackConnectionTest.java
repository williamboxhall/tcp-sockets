package org.example;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.example.domain.Event;
import org.example.infrastructure.Connection;
import org.example.infrastructure.FallbackConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FallbackConnectionTest {
	@Mock
	private Event event;
	private Event anotherEvent;
	@Mock
	private Connection primary;
	@Mock
	private Connection secondary;

	@Test
	public void sendsMessageToPrimaryConnection() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		fallbackConnection.send(event);
		verify(primary).send(event);
		verifyZeroInteractions(secondary);
	}

	@Test
	public void fallsBackToSecondaryWhenPrimaryConnectionFails() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		doThrow(new RuntimeException("expected")).when(primary).send(event);
		fallbackConnection.send(event);
		verify(primary).send(event);
	}

	@Test
	public void continuesToUseSecondaryForSubsequentMessages() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		doThrow(new RuntimeException("expected")).when(primary).send(event);
		fallbackConnection.send(event);
		fallbackConnection.send(anotherEvent);
		verify(primary).send(event);
		verifyNoMoreInteractions(primary);
		verify(secondary).send(event);
		verify(secondary).send(anotherEvent);
	}
}