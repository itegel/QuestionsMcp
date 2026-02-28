package com.codingagent.agent.decision;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.agent.code.CodeAnalyzerAgent;
import com.codingagent.agent.code.CodeGeneratorAgent;
import com.codingagent.agent.code.CodeReviewerAgent;
import com.codingagent.agent.react.ReActAgent;
import com.codingagent.tool.ToolManager;

import java.util.HashMap;
import java.util.Map;

public class AgentRouter {

    private final Map<String, BaseAgent> agents;
    private final ReActAgent reactAgent;

    public AgentRouter(ToolManager toolManager) {
        this.agents = new HashMap<>();
        this.reactAgent = new ReActAgent(toolManager);
        
        registerAgent("analyzer", new CodeAnalyzerAgent());
        registerAgent("generator", new CodeGeneratorAgent());
        registerAgent("reviewer", new CodeReviewerAgent());
        registerAgent("react", reactAgent);
    }

    public void registerAgent(String name, BaseAgent agent) {
        agents.put(name, agent);
    }

    public BaseAgent selectAgent(IntentRecognizer.Intent intent) {
        switch (intent.getType()) {
            case ANALYZE:
            case EXPLAIN:
                return agents.get("analyzer");
            
            case GENERATE:
                return agents.get("generator");
            
            case REVIEW:
                return agents.get("reviewer");
            
            case REFACTOR:
            case DEBUG:
            case TEST:
            case SEARCH:
                return reactAgent;
            
            default:
                return reactAgent;
        }
    }

    public ReActAgent getReActAgent() {
        return reactAgent;
    }

    public BaseAgent getAgent(String name) {
        return agents.get(name);
    }

    public Map<String, String> getAvailableAgents() {
        Map<String, String> descriptions = new HashMap<>();
        for (Map.Entry<String, BaseAgent> entry : agents.entrySet()) {
            descriptions.put(entry.getKey(), entry.getValue().getDescription());
        }
        return descriptions;
    }

    public String getAgentSelectionReasoning(IntentRecognizer.Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("意图：").append(intent.getType().getName()).append("\n");
        sb.append("置信度：").append(intent.getConfidence()).append("\n");
        sb.append("理由：").append(intent.getReasoning()).append("\n");
        sb.append("选择 Agent: ");
        
        BaseAgent selectedAgent = selectAgent(intent);
        if (selectedAgent == reactAgent) {
            sb.append("ReActAgent (智能推理模式)");
        } else {
            sb.append(selectedAgent.getName());
        }
        
        return sb.toString();
    }
}
