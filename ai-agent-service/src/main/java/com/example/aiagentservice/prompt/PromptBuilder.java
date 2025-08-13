package com.example.aiagentservice.prompt;

import org.springframework.ai.chat.prompt.Prompt;

public interface PromptBuilder {
	Prompt createPrompt(String userInput);
}
