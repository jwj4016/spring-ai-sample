package com.example.springaisample.prompt;

import com.example.springaisample.DatabaseSchemaLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class H2PromptBuilder implements PromptBuilder{
	private final DatabaseSchemaLoader databaseSchemaLoader;

	@Override
	public Prompt createPrompt(String userInput) {
		String command = """
				You are an expert in SQL generation using relational databases.
				
				You will receive:
				- A relational database schema (H2-compatible),
				- A user question.
				
				Your task is to:
				1. Decide whether the user's question can be answered *only* by querying the given database schema.
				2. If the answer is NO, respond with the following JSON:
					{
						"answerable": false
					}
				 3. If the answer is YES:
					a. Write a valid H2 SQL query that answers the question.
					b. Describe the query in plain English.
					c. Respond strictly in the following JSON format:
				
					{
						"answerable": true,
						"sql": "SELECT ...",
						"description": "This query ..."
					}
				
				 Instructions:
				 - Only output a JSON object.
				 - Do not add any other commentary or explanation.
				 - Ensure that all table and column names match the schema exactly.
				
				 Database schema:
				 <schema>
				
				 User question:
				 <question>
				""";

		PromptTemplate promptTemplate = PromptTemplate.builder()
				.renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
				.template(command)
				.build();
		promptTemplate.add("schema", databaseSchemaLoader.getSchemaDescription());
		promptTemplate.add("question", userInput);

		return new Prompt(promptTemplate.createMessage());
	}
}
