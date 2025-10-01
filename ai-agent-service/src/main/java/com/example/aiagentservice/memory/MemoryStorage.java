package com.example.aiagentservice.memory;

import java.util.List;

public interface MemoryStorage {

	/**
	 * userId + sessionId별 Memory를 뒤쪽에 추가
	 */
	void addLast(Memory memory);

	/**
	 * 가장 최근 Memory 조회 (뒤쪽)
	 */
	Memory peekLast(String userId, String sessionId);

	/**
	 * 가장 오래된 Memory 조회 (앞쪽)
	 */
	Memory peekFirst(String userId, String sessionId);

	/**
	 * 가장 오래된 Memory 제거 및 반환
	 */
	Memory pollFirst(String userId, String sessionId);

	/**
	 * 해당 user/session의 모든 Memory 삭제
	 */
	void deleteAll(String userId, String sessionId);

	/**
	 * user/session 별 Memory 수 확인
	 */
	int size(String userId, String sessionId);

	/**
	 * user/session 별 최근 n개의 Memory 반환
	 */
	List<Memory> peekLastN(String userId, String sessionId, int end);
}
