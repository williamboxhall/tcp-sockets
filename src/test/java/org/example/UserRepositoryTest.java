package org.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.nio.channels.SocketChannel;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	private static final int UNKNOWN_USER_ID = -1;
	private static final int ANOTHER_USER_ID = 42;
	@InjectMocks
	private UserRepository userRepository;
	@Mock
	private SocketChannel socketChannel;

	@Test
	public void shouldExposeUnknownUsersAsNewUser() {
		assertThat(userRepository.get(UNKNOWN_USER_ID), is(notNullValue()));
	}

	@Test
	public void shouldExposeUnknownUsersAsSocketless() {
		assertThat(userRepository.get(UNKNOWN_USER_ID).getConnection(), is(nullValue()));
	}

	@Test
	@Ignore
	public void shouldUpdateConnectionForUnknownUser() { // TODO factory or reflectomatic
		userRepository.connect(UNKNOWN_USER_ID, socketChannel);
		Connection connection = null;
		assertThat(userRepository.get(UNKNOWN_USER_ID).getConnection(), is(connection));
	}

	@Test
	public void shouldExposeAllUserIds() {
		userRepository.get(UNKNOWN_USER_ID);
		userRepository.connect(ANOTHER_USER_ID, socketChannel);
		assertThat(userRepository.allUserIds(), containsInAnyOrder(UNKNOWN_USER_ID, ANOTHER_USER_ID));
	}
}