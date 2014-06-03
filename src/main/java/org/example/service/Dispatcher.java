package org.example.service;

import java.io.IOException;
import java.util.Set;

import org.example.domain.UserRepository;

public class Dispatcher {
	private UserRepository userRepository;

	public Dispatcher(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void dispatch(String event) throws IOException {
		String[] parts = event.split("\\|");
		String type = parts[1];
		Integer fromUserId = parts.length <= 2 ? null : Integer.valueOf(parts[2]);
		Integer toUserId = parts.length <= 3 ? null : Integer.valueOf(parts[3]);

		switch (type) {
			case "F":
				userRepository.get(toUserId).addFollower(fromUserId);
				writeStringToSocket(toUserId, event, userRepository);
				break;
			case "P":
				// TODO what is there is no following here for private messages?
				writeStringToSocket(toUserId, event, userRepository);
				break;
			case "U":
				userRepository.get(toUserId).removeFollower(fromUserId);
				break; // TODO follow up desired behavior when a message is sent to someone without following
			case "B":
				for (Integer clientId : userRepository.allUserIds()) {
					writeStringToSocket(clientId, event, userRepository);
				}
				break;
			case "S":
				Set<Integer> toUserIds = userRepository.get(fromUserId).getFollowers();
				for (int recipient : toUserIds) {
					writeStringToSocket(recipient, event, userRepository);
				}
				break;
			default:
				throw new Error("Unrecognised event type: " + type);
		}
	}

	private static void writeStringToSocket(int clientId, String event, UserRepository userRepository) throws IOException {
		userRepository.get(clientId).send(event);
	}
}
