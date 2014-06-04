package org.example.service;

import java.io.IOException;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Dispatcher {
	private final UserRepository userRepository;
	private long nextToDispatch = 1;

	public Dispatcher(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	void drainQueuedEventsInOrder(Map<Long, Event> eventQueue) throws IOException {
		while (eventQueue.get(nextToDispatch) != null) {
			Event event = eventQueue.get(nextToDispatch);
			event.type().informUsers(event, userRepository);
			eventQueue.remove(nextToDispatch);
			nextToDispatch++;
		}
	}
}
