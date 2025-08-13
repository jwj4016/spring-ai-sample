package com.example.aiagentservice.prompt;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromptBuilder implements PromptBuilder {

	@Override
	public Prompt createPrompt(String userInput) {
		String command = """
				 You are an expert sales man.
				
				            You will receive:
				            - a user question.
				
				            Your task is to:
				            1. Respond politely and simply.
				
				            User question:
				            <question>
				""";

		PromptTemplate promptTemplate = PromptTemplate.builder()
				.renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
				.template(command)
				.build();
		promptTemplate.add("question", userInput);

		return new Prompt(promptTemplate.createMessage());
	}
}
