package org.example.domain;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;


public class User {
	private final Set<Integer> followers = new HashSet<>();

	public User addFollower(int follower) {
		followers.addAll(new HashSet<>(asList(follower)));
		return this;
	}

	public void removeFollower(int userId) {
		followers.remove(userId);
	}

	public Set<Integer> followers() {
		return new HashSet<>(followers);
	}
}
