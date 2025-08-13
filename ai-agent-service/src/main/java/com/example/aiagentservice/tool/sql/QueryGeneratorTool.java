package com.example.aiagentservice.tool.sql;

import com.example.aiagentservice.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class QueryGeneratorTool implements Function<String, String> {
	private final ChatClient chatClient;
	private final PromptBuilder promptBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final ObjectMapper objectMapper;

	public QueryGeneratorTool(@Qualifier("lightweightChatClient") ChatClient chatClient, @Qualifier("h2PromptBuilder") PromptBuilder promptBuilder, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
		this.chatClient = chatClient;
		this.promptBuilder = promptBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
	}

	@Tool(name = "queryGeneratorTool",
			description = """
				Use this tool only when the user asks a question that can be answered
				by generating a SQL query using the internal relational database schema.
				This includes questions about internal customer and order data, such as:
				- the number of customers,
				- recent orders,
				- or other queries that involve structured data stored in tables.
				
				Do not use this tool for general knowledge, external information, or questions 
				unrelated to structured enterprise data.
			"""
	)
	@Override
	public String apply(String userQuestion) {
		GeneratedQuery generatedQuery = chatClient.prompt(promptBuilder.createPrompt(userQuestion))
				.advisors(new SimpleLoggerAdvisor())
				.call()
				.entity(GeneratedQuery.class);
		log.debug("chatResponse from QueryGenerator ===== " + generatedQuery.toString());

		if(generatedQuery.answerable()) {
			//해당 쿼리를 실행해서
			try {
				validateSql(generatedQuery.sql());
				List<Map<String, Object>> result = jdbcTemplate.queryForList(generatedQuery.sql());
				return objectMapper.writeValueAsString(result);
			} catch (Exception e) {
				log.error(e.getMessage());
				return "can't generate a SQL query";
			}
		}
		return "can't generate a SQL query";
	}

	private void validateSql(String sql) {
		String lowered = sql.toLowerCase(Locale.KOREA);
		if (lowered.contains("delete") || lowered.contains("drop") || lowered.contains("insert") || lowered.contains("update")) {
			throw new IllegalArgumentException("Only SELECT queries are allowed.");
		}

		if (!lowered.contains("select")) {
			throw new IllegalArgumentException("Invalid SQL: not a SELECT query.");
		}
	}
}
