package com.example.aiagentservice;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaLoader {
	private String cachedSchema;
	private final JdbcTemplate jdbcTemplate;

	public String getSchemaDescription() {
		if(cachedSchema != null) return cachedSchema;

		String sql = """
				SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE
				  FROM INFORMATION_SCHEMA.COLUMNS
				 WHERE TABLE_SCHEMA = 'PUBLIC';
				""";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		Map<String, List<String>> tableToColumns = new LinkedHashMap<>();

		for (Map<String, Object> row : rows) {
			String table = row.get("TABLE_NAME").toString();
			String column = row.get("COLUMN_NAME").toString();
			String type = row.get("DATA_TYPE").toString();
			String nullable = row.get("IS_NULLABLE").toString();

			tableToColumns.computeIfAbsent(table, k -> new ArrayList<>())
					.add("COLUMN_NAME : " + column + ", DATA_TYPE : " + type + ", NULLABLE : " + nullable);
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : tableToColumns.entrySet()) {
			builder.append("- ").append(entry.getKey()).append("(")
					.append(String.join(", ", entry.getValue())).append(")\n");
		}

		cachedSchema = builder.toString();
		return cachedSchema;
	}

}
