package org.example.domain;

import static org.example.domain.UserRepository.UserFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	private static final int UNKNOWN_USER_ID = -1;

	@Mock
	private UserFactory userFactory;
	@Mock
	private User user;

	@InjectMocks
	private UserRepository userRepository;

	@Test
	public void shouldExposeUnknownUsersAsNewUser() {
		when(userFactory.create()).thenReturn(user);
		assertThat(userRepository.get(UNKNOWN_USER_ID), is(user));
	}
}