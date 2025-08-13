package com.example.aiagentservice.tool.menu;

import java.util.List;

public record MenuResult(String status, String message, List<Menu> menus) {
}
