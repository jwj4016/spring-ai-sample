package com.example.aiagentservice.memory;

public record Conversation(
		UserMessage userMessage,
		AiMessage llmMessage
) {
}
