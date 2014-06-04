package org.example.service;

import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Dispatcher {
	private final UserRepository userRepository;
	private long nextToDispatch = 1;

	public Dispatcher(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	void drainQueuedEventsInOrder(Map<Long, Event> eventQueue) {
		while (nextEventIsReady(eventQueue)) {
			Event event = eventQueue.get(nextToDispatch);
			event.type().informUsers(event, userRepository);
			eventQueue.remove(nextToDispatch);
			nextToDispatch++;
		}
	}

	private boolean nextEventIsReady(Map<Long, Event> eventQueue) {
		return eventQueue.containsKey(nextToDispatch);
	}
}
