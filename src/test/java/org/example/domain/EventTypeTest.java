package org.example.domain;

import static java.util.Arrays.asList;
import static org.example.domain.EventType.BROADCAST;
import static org.example.domain.EventType.FOLLOW;
import static org.example.domain.EventType.PRIVATE_MESSAGE;
import static org.example.domain.EventType.STATUS_UPDATE;
import static org.example.domain.EventType.UNFOLLOW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class EventTypeTest {
	private static final int JOHN = 34;
	private static final int PAUL = 12;
	private static final int GEORGE = 56;
	private static final int RINGO = 78;
	private static final Set<Integer> ALL_CONNECTED = new HashSet<>(asList(GEORGE, RINGO));

	private UserRepository userRepository;

	@Before
	public void setUp() {
		userRepository = new UserRepository(new UserRepository.UserFactory());
		userRepository.get(GEORGE);
		userRepository.get(RINGO);
		userRepository.get(PAUL);
		userRepository.get(JOHN).addFollower(GEORGE).addFollower(RINGO);
	}

	@Test
	public void followNotifiesAndAddsFollowerToUser() {
		assertThat(userRepository.get(JOHN).followers(), not(hasItems(PAUL)));
		assertThat(FOLLOW.updateAndReturnRecipients(PAUL, JOHN, ALL_CONNECTED, userRepository), is(containsInAnyOrder(JOHN)));
		assertThat(userRepository.get(JOHN).followers(), hasItems(PAUL));
	}

	@Test
	public void unfollowRemovesFollowerAndNotifiesNobody() {
		assertThat(userRepository.get(JOHN).followers(), hasItems(GEORGE));
		assertThat(UNFOLLOW.updateAndReturnRecipients(GEORGE, JOHN, ALL_CONNECTED, userRepository), is(empty()));
		assertThat(userRepository.get(JOHN).followers(), not(hasItems(GEORGE)));
	}

	@Test
	public void broadcastNotifiesEverybody() {
		assertThat(BROADCAST.updateAndReturnRecipients(null, null, ALL_CONNECTED, userRepository), is(ALL_CONNECTED));
	}

	@Test
	public void privateMessageNotifiesRecipient() {
		assertThat(PRIVATE_MESSAGE.updateAndReturnRecipients(null, RINGO, ALL_CONNECTED, userRepository), is(containsInAnyOrder(RINGO)));
	}

	@Test
	public void statusUpdateNotifiesFollowers() {
		assertThat(STATUS_UPDATE.updateAndReturnRecipients(JOHN, null, ALL_CONNECTED, userRepository), is(containsInAnyOrder(GEORGE, RINGO)));
	}

	@Test
	public void fetchableById() {
		assertThat(EventType.byId("F"), is(FOLLOW));
		assertThat(EventType.byId("U"), is(UNFOLLOW));
		assertThat(EventType.byId("B"), is(BROADCAST));
		assertThat(EventType.byId("P"), is(PRIVATE_MESSAGE));
		assertThat(EventType.byId("S"), is(STATUS_UPDATE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegalArgumentExceptionForUnrecognisedId() {
		EventType.byId("X");
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegalArgumentExceptionForNullId() {
		EventType.byId(null);
	}
}