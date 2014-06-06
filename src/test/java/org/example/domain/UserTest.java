package org.example.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {
	private static final int USER_ID = 123;

	@Test
	public void shouldExposeFollowers() {
		User user = new User();
		user.addFollower(USER_ID);
		assertThat(user.followers(), contains(USER_ID));
		user.removeFollower(USER_ID);
		assertThat(user.followers(), not(contains(USER_ID)));
	}
}