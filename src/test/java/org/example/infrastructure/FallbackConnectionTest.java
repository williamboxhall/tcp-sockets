package org.example.infrastructure;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.example.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FallbackConnectionTest {
	@Mock
	private Event event;
	@Mock
	private Event anotherEvent;
	@Mock
	private Connection primary;
	@Mock
	private Connection secondary;

	private FallbackConnection fallbackConnection;

	@Before
	public void setUp() {
		fallbackConnection = new FallbackConnection(primary, secondary, "name");
	}

	@Test
	public void sendsMessageToPrimaryConnection() {
		fallbackConnection.send(event);
		verify(primary).send(event);
		verifyZeroInteractions(secondary);
	}

	@Test
	public void fallsBackToSecondaryWhenPrimaryConnectionFails() {
		doThrow(new RuntimeException("expected")).when(primary).send(event);
		fallbackConnection.send(event);
		verify(primary).send(event);
	}

	@Test
	public void continuesToUseSecondaryForSubsequentMessages() {
		doThrow(new RuntimeException("expected")).when(primary).send(event);
		fallbackConnection.send(event);
		fallbackConnection.send(anotherEvent);
		verify(primary).send(event);
		verifyNoMoreInteractions(primary);
		verify(secondary).send(event);
		verify(secondary).send(anotherEvent);
	}

	@Test
	public void closesBothConnections() {
		fallbackConnection.close();
		verify(primary).close();
		verify(secondary).close();
	}
}