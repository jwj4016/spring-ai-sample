package com.example.aiagentservice.memory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements MemoryStorage {
	Map<String, Map<String, Deque<Memory>>> storage = new ConcurrentHashMap<>();

	@Override
	public void addLast(Memory memory) {
		if(memory == null) throw new NullPointerException("memory is null");
		storage.computeIfAbsent(memory.userId(), k -> new HashMap<>())
				.computeIfAbsent(memory.sessionId(), k -> new ArrayDeque<>())
				.addLast(memory);
	}

	@Override
	public Memory peekLast(String userId, String sessionId) {
		Deque<Memory> memories = storage.getOrDefault(userId, Map.of())
				.getOrDefault(sessionId, new ArrayDeque<>());
		return memories.peekLast();
	}

	@Override
	public Memory peekFirst(String userId, String sessionId) {
		Deque<Memory> memories = storage.getOrDefault(userId, Map.of())
				.getOrDefault(sessionId, new ArrayDeque<>());
		return memories.peekFirst();
	}

	@Override
	public Memory pollFirst(String userId, String sessionId) {
		Deque<Memory> memories = storage.getOrDefault(userId, Map.of())
				.getOrDefault(sessionId, new ArrayDeque<>());
		return memories.pollFirst();
	}

	@Override
	public void deleteAll(String userId, String sessionId) {
		Deque<Memory> memories = storage.getOrDefault(userId, Map.of())
				.getOrDefault(sessionId, new ArrayDeque<>());
		memories.clear();
	}

	@Override
	public int size(String userId, String sessionId) {
		Deque<Memory> memories = storage.getOrDefault(userId, Map.of())
				.getOrDefault(sessionId, new ArrayDeque<>());
		return memories.size();
	}
}
