package com.example.aiagentservice.memory;

import java.time.LocalDateTime;

public record Memory(
		String userId,
		String sessionId,
		Conversation conversation,
		LocalDateTime createdAt
) {
}
