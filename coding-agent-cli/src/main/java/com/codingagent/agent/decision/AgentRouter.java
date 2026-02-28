package com.codingagent.agent.decision;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.agent.code.CodeAnalyzerAgent;
import com.codingagent.agent.code.CodeGeneratorAgent;
import com.codingagent.agent.code.CodeReviewerAgent;
import com.codingagent.agent.react.ReActAgent;
import com.codingagent.agent.reflect.SelfReflectingAgent;
import com.codingagent.tool.ToolManager;

import java.util.HashMap;
import java.util.Map;

public class AgentRouter {

    private final Map<String, BaseAgent> agents;
    private final ReActAgent reactAgent;
    private final SelfReflectingAgent reflectingAgent;

    public AgentRouter(ToolManager toolManager) {
        this.agents = new HashMap<>();
        this.reactAgent = new ReActAgent(toolManager);
        this.reflectingAgent = new SelfReflectingAgent(toolManager);
        
        registerAgent("analyzer", new CodeAnalyzerAgent());
        registerAgent("generator", new CodeGeneratorAgent());
        registerAgent("reviewer", new CodeReviewerAgent());
        registerAgent("react", reactAgent);
        registerAgent("reflecting", reflectingAgent);
    }

    public void registerAgent(String name, BaseAgent agent) {
        agents.put(name, agent);
    }

    public BaseAgent selectAgent(IntentRecognizer.Intent intent) {
        String userInput = (String) intent.getParameters().get("description");
        if (userInput != null) {
            String lowerInput = userInput.toLowerCase();
            if (lowerInput.contains("reflect agent") || lowerInput.contains("reflecting agent") || lowerInput.contains("自我反思") || lowerInput.contains("反思 agent")) {
                return reflectingAgent;
            }
            if (lowerInput.contains("react agent") || lowerInput.contains("推理 agent")) {
                return reactAgent;
            }
            if (lowerInput.contains("analyzer agent") || lowerInput.contains("分析 agent")) {
                return agents.get("analyzer");
            }
            if (lowerInput.contains("generator agent") || lowerInput.contains("生成 agent")) {
                return agents.get("generator");
            }
            if (lowerInput.contains("reviewer agent") || lowerInput.contains("审查 agent")) {
                return agents.get("reviewer");
            }
        }

        switch (intent.getType()) {
            case ANALYZE:
            case EXPLAIN:
                return agents.get("analyzer");
            
            case GENERATE:
                return agents.get("generator");
            
            case REVIEW:
            case REFACTOR:
            case DEBUG:
                return reflectingAgent;
            
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
