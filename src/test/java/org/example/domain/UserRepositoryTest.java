package org.example.domain;

import static org.example.domain.UserRepository.UserFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.net.Socket;

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
	private UserFactory userFactory;
	@Mock
	private User user;
	@Mock
	private User anotherUser;

	@InjectMocks
	private UserRepository userRepository;

	@Test
	public void shouldExposeUnknownUsersAsNewUser() {
		when(userFactory.create()).thenReturn(user);
		assertThat(userRepository.get(UNKNOWN_USER_ID), is(user));
	}

	@Test
	public void shouldExposeAllUserIds() {
		when(userFactory.create()).thenReturn(user).thenReturn(anotherUser);
		userRepository.get(UNKNOWN_USER_ID);
		userRepository.get(ANOTHER_USER_ID);
		assertThat(userRepository.allUserIds(), containsInAnyOrder(UNKNOWN_USER_ID, ANOTHER_USER_ID));
	}
}