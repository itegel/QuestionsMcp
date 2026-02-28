package com.codingagent.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ToolManager {

    private final Map<String, Tool> tools = new HashMap<>();

    public ToolManager() {
        registerTool(new FileTool());
        registerTool(new CommandTool());
        registerTool(new SearchTool());
        registerTool(new DiffTool());
        registerTool(new GitTool());
    }

    public void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
    }

    public void unregisterTool(String toolName) {
        tools.remove(toolName);
    }

    public String executeTool(String toolName, String[] args) {
        Tool tool = tools.get(toolName);
        if (tool == null) {
            return "Tool not found: " + toolName;
        }
        return tool.execute(args);
    }

    public Map<String, Object> executeToolWithMap(String toolName, Map<String, Object> parameters) {
        Tool tool = tools.get(toolName);
        if (tool == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Tool not found: " + toolName);
            return error;
        }
        return tool.executeWithMap(parameters);
    }

    public boolean hasTool(String toolName) {
        return tools.containsKey(toolName);
    }

    public Map<String, String> getAvailableTools() {
        Map<String, String> toolDescriptions = new HashMap<>();
        for (Map.Entry<String, Tool> entry : tools.entrySet()) {
            toolDescriptions.put(entry.getKey(), entry.getValue().getDescription());
        }
        return toolDescriptions;
    }

    public String getToolDescriptions() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Tool> entry : tools.entrySet()) {
            Tool tool = entry.getValue();
            sb.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public List<String> getToolNames() {
        return new ArrayList<>(tools.keySet());
    }

    public Tool getTool(String toolName) {
        return tools.get(toolName);
    }
}
