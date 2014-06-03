package org.example.domain;

import static org.example.infrastructure.Connection.BLACK_HOLE;

public class UserFactory {
	public User create() {
		return new User(BLACK_HOLE);
	}
}
