package org.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	private static final int UNKNOWN_USER_ID = -1;
	@InjectMocks
	private UserRepository userRepository;
	@Mock
	private SocketChannel socket;

	@Test
	public void shouldExposeUnknownUsersAsNewUser() {
		assertThat(userRepository.get(UNKNOWN_USER_ID), is(notNullValue()));
	}

	@Test
	public void shouldExposeUnknownUsersAsSocketless() {
		assertThat(userRepository.get(UNKNOWN_USER_ID).getSocketChannel(), is(nullValue()));
	}

	@Test
	public void shouldConnectSocketsForUnknownUser() {
		userRepository.connect(UNKNOWN_USER_ID, socket);
		assertThat(userRepository.get(UNKNOWN_USER_ID).getSocketChannel(), is(socket));
	}
}