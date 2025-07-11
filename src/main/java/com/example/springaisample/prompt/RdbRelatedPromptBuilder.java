package com.example.springaisample.prompt;

import com.example.springaisample.DatabaseSchemaLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RdbRelatedPromptBuilder implements PromptBuilder {
	private final DatabaseSchemaLoader databaseSchemaLoader;

	@Override
	public Prompt createPrompt(String userInput) {
		String command = """
				 You are an expert in SQL generation using relational databases.
				
				            You will receive:
				            - a relational database schema (H2-compatible),
				            - a user question.
				
				            Your task is to:
				            1. Decide whether the user's question can be answered *only* by querying the given database schema.
				            2. Respond strictly with 'YES' if it is possible, or 'NO' if not.
				            3. Do not explain anything else. Do not include the query. Only reply with 'YES' or 'NO'.
				
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
