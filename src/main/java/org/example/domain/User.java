package org.example.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class User {
	private final Set<Integer> followers = new HashSet<>();

	public void addFollower(Integer... follower) {
		followers.addAll(new HashSet<>(Arrays.asList(follower)));
	}

	public void removeFollower(int userId) {
		followers.remove(userId);
	}

	public Set<Integer> getFollowers() {
		return new HashSet<>(followers);
	}
}
