package org.example.service;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import org.example.domain.Event;
import org.example.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RouterTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private Event event;
	@Mock
	private Socket first;
	@Mock
	private Socket second;
	@Mock
	private OutputStream firstOut;
	@Mock
	private OutputStream secondOut;

	Router router;

	@Before
	public void setUp() throws IOException {
		when(first.getOutputStream()).thenReturn(firstOut);
		when(second.getOutputStream()).thenReturn(secondOut);
		router = new Router(userRepository, new HashMap<Integer, Socket>());
	}

	@Test
	public void notifiesConnectedRecipientsOfEvent() throws IOException {
		when(event.updateAndReturnRecipients(userRepository)).thenReturn(new HashSet<>(asList(1, 2)));

		router.connect(1, first);
		router.accept(event);

		verify(firstOut).write(any(byte[].class), anyInt(), anyInt());
		verifyZeroInteractions(secondOut);
	}

	@Test
	public void disconnectsRecipientAfterIOException() throws IOException {
		when(event.updateAndReturnRecipients(userRepository)).thenReturn(new HashSet<>(asList(1, 2)));
		when(first.getOutputStream()).thenThrow(new IOException("expected"));

		router.connect(1, first);
		router.connect(2, second);
		router.accept(event);
		router.accept(event);

		verify(secondOut, times(2)).write(any(byte[].class), anyInt(), anyInt());
		verify(first, times(1)).getOutputStream();
		verifyNoMoreInteractions(first);
	}
}