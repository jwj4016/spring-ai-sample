package com.example.aiagentservice.memory;

import java.time.LocalDateTime;

public record AiMessage(
		String message,
		Integer token,
		LocalDateTime createdAt
) {
}
