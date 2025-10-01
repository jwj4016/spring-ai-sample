package com.example.aiagentservice.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class RedisMemoryStorage implements MemoryStorage {
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private String getKey(String userId, String sessionId) {
		return String.format("memory:%s:%s", userId, sessionId);
	}

	private String toJson(Memory memory) {
		try {
			return objectMapper.writeValueAsString(memory);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize memory", e);
		}
	}

	private Memory fromJson(String json) {
		try {
			return objectMapper.readValue(json, Memory.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to deserialize memory", e);
		}
	}

	@Override
	public void addLast(Memory memory) {
		redisTemplate.opsForList().rightPush(getKey(memory.userId(), memory.sessionId()), toJson(memory));
	}

	@Override
	public Memory peekLast(String userId, String sessionId) {
		String value = (String) redisTemplate.opsForList().index(getKey(userId, sessionId), -1);
		return Optional.ofNullable(value).map(this::fromJson).orElse(null);
	}

	@Override
	public Memory peekFirst(String userId, String sessionId) {
		String value = (String) redisTemplate.opsForList().index(getKey(userId, sessionId), 0);
		return Optional.ofNullable(value).map(this::fromJson).orElse(null);
	}

	@Override
	public Memory pollFirst(String userId, String sessionId) {
		String value = (String) redisTemplate.opsForList().leftPop(getKey(userId, sessionId));
		return Optional.ofNullable(value).map(this::fromJson).orElse(null);
	}

	@Override
	public void deleteAll(String userId, String sessionId) {
		redisTemplate.delete(getKey(userId, sessionId));
	}

	@Override
	public int size(String userId, String sessionId) {
		Long size = redisTemplate.opsForList().size(getKey(userId, sessionId));
		return size == null ? 0 : size.intValue();
	}
}
