package com.example.springaisample.tool.menu;

import java.util.List;

public record MenuResult(String status, String message, List<Menu> menus) {
}
