package org.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {
	private static final int USER_ID = 123;
	@Mock
	private Connection connection;
	@InjectMocks
	private User user;

	@Test
	public void shouldExposeConnection() { // TODO gross.
		user.setSocketChannel(connection);
		assertThat(user.getConnection(), is(connection));
	}

	@Test
	public void shouldExposeFollowers() { // TODO also gross.
		user.addFollower(USER_ID);
		assertThat(user.getFollowers(), contains(USER_ID));
		user.removeFollower(USER_ID);
		assertThat(user.getFollowers(), not(contains(USER_ID)));
	}
}