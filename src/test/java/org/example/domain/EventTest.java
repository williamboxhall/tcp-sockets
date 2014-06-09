package org.example.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private User user;

	@Test
	public void parsesPropertiesFromInput() {
		Event event = new Event("1|F|2|3");
		assertThat(event.raw(), is("1|F|2|3"));
		assertThat(event.sequenceNumber(), is(1L));
	}

	@Test
	public void managesRecipients() {
		when(userRepository.get(3)).thenReturn(user);
		assertThat(new Event("1|F|2|3").updateAndReturnRecipients(Collections.<Integer>emptySet(), userRepository), containsInAnyOrder(3));
		verify(user).addFollower(2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForNullString() {
		new Event(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForEmptyString() {
		new Event("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForWhitespaceString() {
		new Event(" ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForNonNumericSequenceNumber() {
		new Event("a|B");
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForUnrecognisedEventType() {
		new Event("1|X");
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForNonNumericFromUserId() {
		new Event("1|S|a");
	}

	@Test(expected = IllegalArgumentException.class)
	public void IllegalArgumentExceptionForNonNumericToUserId() {
		new Event("1|F|2|a");
	}
}