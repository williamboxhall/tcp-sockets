package org.example.domain;


import static org.example.infrastructure.ConnectionFactory.BLACK_HOLE;

public class UserFactory {
	public User create() {
		return new User(BLACK_HOLE);
	}
}
