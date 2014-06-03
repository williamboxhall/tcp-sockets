package org.example.domain;

import java.util.HashSet;
import java.util.Set;

import org.example.infrastructure.Connection;

public class User {
	private final Set<Integer> followers = new HashSet<>();
	private Connection connection;

	public User(Connection connection) {
		this.connection = connection;
	}

	public void send(Event event) {
		connection.send(event);
	}

	public void updateConnection(Connection connection) {
		disconnect();
		this.connection = connection;
	}

	public void disconnect() {
		connection.close();
	}

	public void addFollower(int userId) {
		followers.add(userId);
	}

	public void removeFollower(int userId) {
		followers.remove(userId);
	}

	// TODO consider making User event-centric
	public Set<Integer> getFollowers() {
		return new HashSet<>(followers);
	}
}
