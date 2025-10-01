package com.example.aiagentservice.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;


@Testcontainers
public class RedisMemoryStorageTest extends AbstractMemoryStorageTest{
	private RedisTemplate<String, Object> redisTemplate;

	@Container
	static GenericContainer<?> redisContainer =
			new GenericContainer<>("redis:7-alpine")
					.withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", redisContainer::getHost);
		registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
	}

	@Override
	protected MemoryStorage createMemoryStorage() {
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
				redisContainer.getHost(), redisContainer.getMappedPort(6379));
		connectionFactory.afterPropertiesSet();

		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		return new RedisMemoryStorage(redisTemplate, objectMapper);
	}

	@AfterEach
	void cleanRedis() {
		// RedisTemplate 키 삭제
		Set<String> keys = redisTemplate.keys("memory:user1:session1");
		if (!keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}
}
