package com.example.springaisample;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class TestController {
	private final ChatClient chatClient;
	private final AiStreamService aiStreamService;

	@GetMapping("/ai")
	String testAi(String userInput, String role) {
		return chatClient
				.prompt()
//				.system(promptSystemSpec -> promptSystemSpec.param("role", role))
				.user(userInput)
				.call()
				.content();
	}

	@GetMapping("/ai/chatResponse")
	ChatResponse testAiWithChatResponse(String userInput) {
		return chatClient
				.prompt()
//				.system(promptSystemSpec -> promptSystemSpec.param("role", role))
				.user(userInput)
				.call()
				.chatResponse();
	}


	@CrossOrigin(origins = "*")
	@GetMapping(value = "/stream/ai", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> testAiUsingStreaming(String userInput, String conversationId) {
		return aiStreamService.streamAiResponse(userInput, conversationId);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/stream/ai/chatResponse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ChatResponse> testAiUsingStreamingAndChatResponse(String userInput) {
		return chatClient.prompt()
//				.system(promptSystemSpec -> promptSystemSpec.param("role", role))
				.user(userInput)
				.stream()
				.chatResponse();
	}

}

