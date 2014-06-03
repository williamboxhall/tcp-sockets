package org.example.infrastructure;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.example.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SocketConnectionTest {
	private static final String RAW_EVENT = "1|B";
	private static final Event EVENT = new Event(RAW_EVENT);
	@Mock
	private Socket socket;
	@Mock
	private OutputStream outputStream;
	private SocketConnection socketConnection;

	@Before
	public void setUp() {
		socketConnection = new SocketConnection(socket);
	}

	@Test
	public void sendShouldWriteRawEventToOutput() throws IOException {
		when(socket.getOutputStream()).thenReturn(outputStream);
		socketConnection.send(EVENT);
		verify(outputStream).write(Mockito.<byte[]>any(), anyInt(), anyInt());
	}

	@Test(expected = RuntimeException.class)
	public void wrapsIOExceptionAsRuntimeExceptionWhenOutputStreamFails() throws IOException {
		when(socket.getOutputStream()).thenThrow(new IOException("expected"));
		socketConnection.send(EVENT);
	}

	@Test
	public void closeShouldCloseSocket() throws IOException {
		socketConnection.close();
		verify(socket).close();
	}

	@Test
	public void closeShouldSwallowAnyIOExceptionsThrownDuringClose() throws IOException {
		doThrow(new IOException("expected")).when(socket).close();
		socketConnection.close();
		verify(socket).close();
	}
}