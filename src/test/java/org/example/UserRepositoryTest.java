package org.example;

import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.Socket;

import org.example.domain.User;
import org.example.domain.UserFactory;
import org.example.domain.UserRepository;
import org.example.infrastructure.Connection;
import org.example.infrastructure.ConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	private static final int UNKNOWN_USER_ID = -1;
	private static final int ANOTHER_USER_ID = 42;

	@Mock
	private Socket socket;
	@Mock
	private ConnectionFactory connectionFactory;
	@Mock
	private UserFactory userFactory;
	@Mock
	private User user;
	@Mock
	private User anotherUser;
	@Mock
	private Connection connection;

	@InjectMocks
	private UserRepository userRepository;

	@Test
	public void shouldExposeUnknownUsersAsNewUser() {
		when(userFactory.create()).thenReturn(user);
		assertThat(userRepository.get(UNKNOWN_USER_ID), is(user));
	}

	@Test
	public void shouldUpdateConnectionForUnknownUser() {
		when(userFactory.create()).thenReturn(user);
		when(connectionFactory.createFor(socket, valueOf(UNKNOWN_USER_ID))).thenReturn(connection);
		userRepository.connect(UNKNOWN_USER_ID, socket);
		verify(user).updateConnection(connection);
	}

	@Test
	public void shouldExposeAllUserIds() {
		when(userFactory.create()).thenReturn(user).thenReturn(anotherUser);
		userRepository.get(UNKNOWN_USER_ID);
		userRepository.connect(ANOTHER_USER_ID, socket);
		assertThat(userRepository.allUsers(), containsInAnyOrder(user, anotherUser));
	}
}