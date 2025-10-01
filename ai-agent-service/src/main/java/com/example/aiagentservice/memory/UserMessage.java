package com.example.aiagentservice.memory;

import java.time.LocalDateTime;

public record UserMessage(
		String message,
		Integer token,
		LocalDateTime createdAt
) {
}
