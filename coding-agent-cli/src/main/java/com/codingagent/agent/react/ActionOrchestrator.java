package com.codingagent.agent.react;

import com.codingagent.tool.ToolManager;

import java.util.Map;

public class ActionOrchestrator {

    private final ToolManager toolManager;

    public ActionOrchestrator(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public String execute(Action action) {
        if (action == null) {
            return "No action to execute";
        }

        String toolName = action.getToolName();
        Map<String, Object> parameters = action.getParameters();

        if (!toolManager.hasTool(toolName)) {
            return "Error: Tool '" + toolName + "' not found. Available tools: " + 
                   String.join(", ", toolManager.getToolNames());
        }

        try {
            Map<String, Object> result = toolManager.executeToolWithMap(toolName, parameters);
            if (result.containsKey("error")) {
                return "Error executing tool: " + result.get("error");
            }
            return result.getOrDefault("result", "Action executed successfully").toString();
        } catch (Exception e) {
            return "Error executing action: " + e.getMessage();
        }
    }

    public boolean isToolAvailable(String toolName) {
        return toolManager.hasTool(toolName);
    }

    public String getAvailableTools() {
        return toolManager.getToolDescriptions();
    }
}
