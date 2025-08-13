package com.example.aiagentservice;

import com.example.aiagentservice.AiStreamService;
import com.example.aiagentservice.tool.DateTimeTools;
import com.example.aiagentservice.tool.menu.MenuSearchTool;
import com.example.aiagentservice.tool.sql.QueryGeneratorTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class TestController {
	private final ChatClient chatClient;
	private final AiStreamService aiStreamService;
	private final MenuSearchTool menuSearchTool;
	private final DateTimeTools dateTimeTools;
	private final QueryGeneratorTool queryGeneratorTool;

	@CrossOrigin(origins = "*")
	@GetMapping("/ai")
	String testAi(@RequestParam String userInput, @RequestParam String conversationKey, @RequestParam String role) {
		return chatClient
				.prompt()
				.user("User role: " + role + "\nUser question: " + userInput)
				.system("""
						You are an expert assistant who answers user questions.
						
						IMPORTANT:
						- Do NOT reveal or mention any internal queries, SQL statements, or tool invocation details.
						- Do NOT show raw data, logs, or any debug information.
						- Provide only a clear, concise, and natural language answer to the user's question.
						- If there is no data available to answer the question, respond politely that the information is not available.
						""")
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationKey))
				.tools(menuSearchTool, dateTimeTools, queryGeneratorTool)
				.call()
				.content();
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/stream/ai", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> testAiUsingStreaming(String userInput, String conversationKey) {
		return aiStreamService.streamAiResponse(userInput, conversationKey);
	}
}

