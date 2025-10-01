package com.example.aiagentservice.memory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractMemoryStorageTest {
	protected MemoryStorage memoryStorage;
	protected final String userId = "user1";
	protected final String sessionId = "session1";

	@BeforeEach
	void setUp() {
		memoryStorage = createMemoryStorage();
	}

	/**
	 * 구현체에서 자신이 테스트할 MemoryStorage를 리턴
	 */
	protected abstract MemoryStorage createMemoryStorage();

	protected Memory createMemory(String userId, String sessionId, String question, String answer) {
		LocalDateTime now = LocalDateTime.now();
		UserMessage userMessage = new UserMessage(question, 5, now);
		AiMessage aiMessage = new AiMessage(answer, 5, now);
		Conversation conversation = new Conversation(userMessage, aiMessage);
		return new Memory(userId, sessionId, conversation, now);
	}

	@Test
	void 최근_기억을_추가할수있다() {
		//given
		Memory memory = createMemory(userId, sessionId, "이것은 사용자의 질문입니다.", "이것은 llm의 응답입니다.");

		//when
		memoryStorage.addLast(memory);

		//then
		Memory lastMemory = memoryStorage.peekLast(userId, sessionId);
		assertNotNull(lastMemory);
		assertEquals(memory, lastMemory);
	}

	@Test
	void 가장_오래된_기억을_조회할_수_있다() {
		// given
		Memory memory1 = createMemory(userId, sessionId, "질문1", "응답1");
		Memory memory2 = createMemory(userId, sessionId, "질문2", "응답2");
		Memory memory3 = createMemory(userId, sessionId, "질문3", "응답3");

		memoryStorage.addLast(memory1);
		memoryStorage.addLast(memory2);
		memoryStorage.addLast(memory3);

		// when
		Memory firstMemory = memoryStorage.peekFirst(userId, sessionId);

		//then
		assertNotNull(firstMemory);
		assertEquals(memory1, firstMemory);
	}

	@Test
	void 가장_오래된_기억을_조회하면서_삭제할_수_있다() {
		// given
		Memory memory1 = createMemory(userId, sessionId, "질문4", "응답4");
		Memory memory2 = createMemory(userId, sessionId, "질문5", "응답5");
		Memory memory3 = createMemory(userId, sessionId, "질문6", "응답6");

		memoryStorage.addLast(memory1);
		memoryStorage.addLast(memory2);
		memoryStorage.addLast(memory3);

		// when
		Memory firstMemory = memoryStorage.pollFirst(userId, sessionId);

		//then
		assertNotNull(firstMemory);
		assertEquals(memory1, firstMemory);
		assertEquals(memory2, memoryStorage.peekFirst(userId, sessionId));
	}

	@Test
	void 유저의_세션에_해당하는_기억을_모두_삭제할_수_있다() {
		// given
		memoryStorage.addLast(createMemory(userId, sessionId, "질문7", "응답7"));
		memoryStorage.addLast(createMemory(userId, sessionId, "질문8", "응답8"));
		memoryStorage.addLast(createMemory(userId, sessionId, "질문9", "응답9"));

		// when
		memoryStorage.deleteAll(userId, sessionId);

		// then
		assertNull(memoryStorage.peekFirst(userId, sessionId));
		assertNull(memoryStorage.pollFirst(userId, sessionId));
	}

	@Test
	void 저장된_기억의_크기를_조회할_수_있다() {
		// given
		memoryStorage.addLast(createMemory(userId, sessionId, "질문10", "응답10"));
		memoryStorage.addLast(createMemory(userId, sessionId, "질문11", "응답11"));
		memoryStorage.addLast(createMemory(userId, sessionId, "질문12", "응답12"));

		// when
		int size = memoryStorage.size(userId, sessionId);

		// then
		assertEquals(3, size);
	}
}
