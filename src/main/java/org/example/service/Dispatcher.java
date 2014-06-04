package org.example.service;

import java.io.IOException;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Dispatcher {
	private final UserRepository userRepository;
	private long nextToDispatch;

	public Dispatcher(UserRepository userRepository, long eventSeqNoOffset) {
		this.userRepository = userRepository;
		this.nextToDispatch = eventSeqNoOffset + 1;
	}

	void drainQueuedEventsInOrder(Map<Long, Event> eventQueue) throws IOException {
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
