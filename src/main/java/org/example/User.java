package org.example;

import java.util.HashSet;
import java.util.Set;

public class User {
	private final Set<Integer> followers = new HashSet<>();
	private Connection connection;

	public User(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setSocketChannel(Connection connection) {
		this.connection = connection;
	}

	public void addFollower(int userId) {
		followers.add(userId);
	}

	public void removeFollower(int userId) {
		followers.remove(userId);
	}

	public Set<Integer> getFollowers() {
		return new HashSet<>(followers);
	}
}
