package com.example.springaisample.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
	@Bean
	ChatMemory chatMemory() {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(new InMemoryChatMemoryRepository())
				.maxMessages(100)
				.build();
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder
				.defaultSystem("You are a helpful DBA who speaks like a RDB expert.")
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory()).build()
						, new SimpleLoggerAdvisor()
				)
				.build();

	}

}
