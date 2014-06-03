package org.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.example.domain.Event;
import org.example.domain.User;
import org.example.infrastructure.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {
	private static final int USER_ID = 123;

	@Mock
	private Event event;
	@Mock
	private Connection connection;
	@Mock
	private Connection anotherConnection;

	private User user;

	@Before
	public void setUp() {
		user = new User(connection);
	}

	@Test
	public void shouldForwardEventsToConnection() {
		user.send(event);
		verify(connection).send(event);
	}

	@Test
	public void shouldUpdateConnection() {
		user.updateConnection(anotherConnection);
		user.send(event);
		verify(anotherConnection).send(event);
		verifyZeroInteractions(connection);
	}

	@Test
	public void shouldExposeFollowers() { // TODO gross.
		user.addFollower(USER_ID);
		assertThat(user.getFollowers(), contains(USER_ID));
		user.removeFollower(USER_ID);
		assertThat(user.getFollowers(), not(contains(USER_ID)));
	}
}