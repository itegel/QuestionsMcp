package com.codingagent.agent.base;

import com.codingagent.memory.MemoryService;
import com.codingagent.tool.ToolManager;
import com.codingagent.util.QwenClient;

public abstract class BaseAgent {

    protected String name;
    protected String role;
    protected QwenClient qwenClient;
    protected ToolManager toolManager;
    protected MemoryService memoryService;

    public BaseAgent(String name, String role) {
        this.name = name;
        this.role = role;
        this.qwenClient = new QwenClient();
        this.toolManager = new ToolManager();
        this.memoryService = new MemoryService();
    }

    public abstract String process(String sessionId, String task);

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
    
    public String getDescription() {
        return role;
    }

}
