package com.example.springaisample;

import com.example.springaisample.prompt.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiStreamService {
	private static final String DONE_SIGNAL = "\"done\":true";
	private static final String YES = "YES";

	private final ChatClient chatClient;
	private final Map<String, PromptBuilder> promptBuilders;

	public Flux<String> streamAiResponse(String userInput, String conversationId) {
		// 1. RDB를 활용해 응답 가능한지 판단(LLM이 판단한다.)
		String rdbCheckResult = chatClient.prompt(
						promptBuilders.get("rdbRelatedPromptBuilder").createPrompt(userInput))
				.call()
				.content();

		// 2. 적절한 프롬프트 선택
		PromptBuilder selectedPromptBuilder = YES.equals(rdbCheckResult)
				? promptBuilders.get("queryExpertPromptBuilder")
				: promptBuilders.get("defaultPromptBuilder");

		// 3. 스트리밍 응답 생성
		return chatClient.prompt(selectedPromptBuilder.createPrompt(userInput))
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
				.stream()
				.content()
				.takeUntil(s -> s.contains(DONE_SIGNAL));
	}
}
