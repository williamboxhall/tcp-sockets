package org.example;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FallbackConnectionTest {
	private static final String MESSAGE = "foo bar baz";
	private static final String ANOTHER_MESSAGE = "quux";
	@Mock
	private Connection primary;
	@Mock
	private Connection secondary;

	@Test
	public void sendsMessageToPrimaryConnection() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		fallbackConnection.send(MESSAGE);
		verify(primary).send(MESSAGE);
		verifyZeroInteractions(secondary);
	}

	@Test
	public void fallsBackToSecondaryWhenPrimaryConnectionFails() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		doThrow(new RuntimeException("expected")).when(primary).send(MESSAGE);
		fallbackConnection.send(MESSAGE);
		verify(primary).send(MESSAGE);
	}

	@Test
	public void continuesToUseSecondaryForSubsequentMessages() {
		FallbackConnection fallbackConnection = new FallbackConnection(primary, secondary);
		doThrow(new RuntimeException("expected")).when(primary).send(MESSAGE);
		fallbackConnection.send(MESSAGE);
		fallbackConnection.send(ANOTHER_MESSAGE);
		verify(primary).send(MESSAGE);
		verifyNoMoreInteractions(primary);
		verify(secondary).send(MESSAGE);
		verify(secondary).send(ANOTHER_MESSAGE);
	}
}