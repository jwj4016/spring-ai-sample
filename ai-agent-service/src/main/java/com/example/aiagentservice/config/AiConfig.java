package com.example.aiagentservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class AiConfig {

	@Bean
	public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
		return JdbcChatMemoryRepository.builder()
				.jdbcTemplate(jdbcTemplate)
				.dialect(new CustomChatMemoryRepositoryDialect())
				.build();
	}

	@Bean
	ChatMemory chatMemory(ChatMemoryRepository repository) {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(repository)
//				.chatMemoryRepository(new InMemoryChatMemoryRepository())
				.maxMessages(100)
				.build();
	}

	@Bean
	@Qualifier("defaultChatClient")
	ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
		return builder
//				.defaultSystem("You are a helpful DBA who speaks like a RDB expert.")
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory).build()
						, new SimpleLoggerAdvisor()
				)
				.build();
	}

	@Bean
	@Qualifier("lightweightChatClient")
	public ChatClient lightweightChatClient(ChatClient.Builder builder) {
		return builder.build();
	}

}
