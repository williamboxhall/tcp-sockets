package org.example.service;

import java.io.IOException;

import org.example.domain.Event;
import org.example.domain.UserRepository;

public class Dispatcher {
	private UserRepository userRepository;

	public Dispatcher(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void dispatch(String eventString) throws IOException {
		Event event = new Event(eventString);
		event.getType().informUsers(event, userRepository);
	}

}
