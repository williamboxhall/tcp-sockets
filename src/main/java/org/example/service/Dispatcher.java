package org.example.service;

import java.io.IOException;
import java.util.Map;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Dispatcher {
	private UserRepository userRepository;
	private long lastDispatchedSeqNo = 0;

	public Dispatcher(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	void drainQueuedEventsInOrder(Map<Long, Event> eventQueue) throws IOException {
		while (!eventQueue.isEmpty()) {
			lastDispatchedSeqNo++;
			Event event = eventQueue.get(lastDispatchedSeqNo);
			event.getType().informUsers(event, userRepository);
			eventQueue.remove(lastDispatchedSeqNo);
		}
	}
}
