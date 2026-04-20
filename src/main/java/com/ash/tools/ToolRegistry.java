package com.ash.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    public void registerTool(String name, Tool tool) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        if (tool == null) {
            throw new IllegalArgumentException("Tool cannot be null");
        }
        tools.put(name, tool);
    }

    public Tool getTool(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return tools.get(name);
    }

    public boolean hasTool(String name) {
        return name != null && tools.containsKey(name);
    }
}