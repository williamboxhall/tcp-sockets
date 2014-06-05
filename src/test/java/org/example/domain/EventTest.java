package org.example.domain;

import static org.example.domain.EventType.BROADCAST;
import static org.example.domain.EventType.FOLLOW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.Test;

public class EventTest {
	@Test
	public void parsesPropertiesFromInput() {
		Event event = new Event("1|F|2|3");
		assertThat(event.raw(), is("1|F|2|3"));
		assertThat(event.sequenceNumber(), is(1L));
		assertThat(event.type(), is(FOLLOW));
		assertThat(event.fromUser(), is(2));
		assertThat(event.toUser(), is(3));
	}

	@Test
	public void lazyNullPointersOnMissingUserIds() {
		Event event = new Event("1|B");
		assertThat(event.raw(), is("1|B"));
		assertThat(event.sequenceNumber(), is(1L));
		assertThat(event.type(), is(BROADCAST));
		try {
			event.fromUser();
			fail("fromUserId should have thrown NPE");
		} catch (NullPointerException e) {
		}
		try {
			event.toUser();
			fail("toUserId should have thrown NPE");
		} catch (NullPointerException e) {
		}
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