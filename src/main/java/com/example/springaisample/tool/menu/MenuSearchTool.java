package com.example.springaisample.tool.menu;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuSearchTool implements Function<String, MenuResult> {
	private static final List<Menu> menus = List.of(
			new Menu("https://example.com/v1/a", "You can search Users here.", new String[]{"SC", "SL", "CM", "AM"}),
			new Menu("https://example.com/v1/b", "You can create User here.", new String[]{"SC", "SL"}),
			new Menu("https://example.com/v1/c", "You can register Account here.", new String[]{"CM", "AM"}),
			new Menu("https://example.com/v1/d", "You can Delete Account here.", new String[]{"AM"}),
			new Menu("https://example.com/v1/e", "You can search Cars here.", new String[]{"CM"})
	);

	@Tool(
			name = "menuListByRole",
			description = """
				Returns a list of menus the user can access based on their role.
				
				Input JSON format:
				{
				  "role": "CM"
				}
				
				Output JSON:
				{
					"status": "ok" | "not_found",
					"message": "Some explanation",
					"menus": [ { "url": "...", "description": "...", "roles": [...] } ]
				}
					
				If status is "not_found", inform the user that there are no menus for the role.
			"""
	)
	@Override
	public MenuResult apply(String role) {
		if (StringUtils.isBlank(role))
			return new MenuResult("not_found", "No role provided", List.of());

		List<Menu> filtered = menus.stream()
				.filter(menu -> Arrays.asList(menu.roles()).contains(role))
				.toList();

		if (filtered.isEmpty()) {
			return new MenuResult("not_found", "No menus found for role: " + role, List.of());
		}

		return new MenuResult("ok", "Menus found for role: " + role, filtered);
	}
}
