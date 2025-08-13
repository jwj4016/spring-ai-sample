package com.example.aiagentservice.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;

public class CustomChatMemoryRepositoryDialect implements JdbcChatMemoryRepositoryDialect {

	@Override
	public String getSelectMessagesSql() {
		return "SELECT content, type FROM SPRING_AI_CHAT_MEMORY WHERE conversation_key = ? ORDER BY create_time ASC";
	}

	@Override
	public String getInsertMessageSql() {
		return "INSERT INTO SPRING_AI_CHAT_MEMORY (conversation_key, content, type, create_time) VALUES (?, ?, ?, ?)";
	}

	@Override
	public String getSelectConversationIdsSql() {
		return "SELECT DISTINCT conversation_key FROM SPRING_AI_CHAT_MEMORY";
	}

	@Override
	public String getDeleteMessagesSql() {
		return "DELETE FROM SPRING_AI_CHAT_MEMORY WHERE conversation_key = ?";
	}
}
