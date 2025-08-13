package com.example.aiagentservice.prompt;

import com.example.aiagentservice.DatabaseSchemaLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class QueryExpertPromptBuilder implements PromptBuilder {
	private final DatabaseSchemaLoader databaseSchemaLoader;

	@Override
	public Prompt createPrompt(String userInput) {
		String command = """
				 You are an expert in SQL generation using relational databases.
				
				            You will receive:
				            - a relational database schema (H2-compatible),
				
				            Your task is to:
				            1. Write a valid H2 SQL query that answers the question.
				            2. Describe the query in plain English.
				            3. Respond strictly in JSON format as shown.
				
				            Database schema:
				            <schema>
				
				            Respond in the following format only:
				            {
				              "sql": "SELECT ...",
				              "description": "This query ..."
				            }
				""";
		PromptTemplate systemPromptTemplate = SystemPromptTemplate.builder()
				.renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
				.template(command)
				.build();
		systemPromptTemplate.add("schema", databaseSchemaLoader.getSchemaDescription());

		Message userMessage = new UserMessage(userInput);
		Message systemMessage = systemPromptTemplate.createMessage();

		return new Prompt(List.of(userMessage, systemMessage));
	}
}
