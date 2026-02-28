package com.codingagent.agent.manager;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.agent.code.CodeAnalyzerAgent;
import com.codingagent.agent.code.CodeGeneratorAgent;
import com.codingagent.agent.code.CodeReviewerAgent;

import java.util.HashMap;
import java.util.Map;

public class AgentManager {

    private final Map<String, BaseAgent> agents = new HashMap<>();

    public AgentManager() {
        // 初始化各种 Agent
        agents.put("analyzer", new CodeAnalyzerAgent());
        agents.put("generator", new CodeGeneratorAgent());
        agents.put("reviewer", new CodeReviewerAgent());
    }

    public String processTask(String sessionId, String agentType, String task) {
        BaseAgent agent = agents.get(agentType);
        if (agent == null) {
            return "Agent not found: " + agentType;
        }
        return agent.process(sessionId, task);
    }

    public Map<String, String> getAvailableAgents() {
        Map<String, String> agentInfo = new HashMap<>();
        for (Map.Entry<String, BaseAgent> entry : agents.entrySet()) {
            agentInfo.put(entry.getKey(), entry.getValue().getRole());
        }
        return agentInfo;
    }

}
