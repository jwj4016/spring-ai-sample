package com.example.aiagentservice.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeTools {
	@Tool(
			//툴의 이름. 지정하지 않으면 default로 메소드명 사용.
			name = "getCurrentDateTime"
			// 툴의 설명. llm이 이 설명을 사용해 해당 기능을 언제&어떻게 호출할지 이해한다.
			// 지정하지 않으면 메소드명이 설명으로 사용된다.
			, description = "Get the current date and time in the user's timezone"
			// 툴의 처리 결과를 llm에 전달할지, llm에 전달하지 않고 바로 caller에게 return할지 결정. default = false
			, returnDirect = true //tool의 처리 결과를 바로 caller에게 return 한다.
			//, resultConverter =
	)
	String getCurrentDateTime() {
		return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
	}

	@Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format")
	void setAlarm(@ToolParam(description = "Time in ISO-8601 format", required = true)/* 툴 파라미터에 대한 정보를 제공한다. */ String time) {
		LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
		System.out.println("Alarm set for " + alarmTime);
	}
}
