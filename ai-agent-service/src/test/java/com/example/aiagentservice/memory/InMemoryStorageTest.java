package com.example.aiagentservice.memory;

public class InMemoryStorageTest extends AbstractMemoryStorageTest {
	@Override
	protected MemoryStorage createMemoryStorage() {
		return new InMemoryStorage();
	}
}
