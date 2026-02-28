package com.codingagent.agent.collaboration;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.agent.code.CodeAnalyzerAgent;
import com.codingagent.agent.code.CodeGeneratorAgent;
import com.codingagent.agent.react.ReActAgent;
import com.codingagent.agent.reflect.SelfReflectingAgent;
import com.codingagent.tool.ToolManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiAgentCoordinator {

    private final Map<String, BaseAgent> agents;
    private final ToolManager toolManager;
    private final List<String> executionHistory;

    public MultiAgentCoordinator(ToolManager toolManager) {
        this.toolManager = toolManager;
        this.agents = new ConcurrentHashMap<>();
        this.executionHistory = new ArrayList<>();
        initializeAgents();
    }

    private void initializeAgents() {
        agents.put("analyzer", new CodeAnalyzerAgent());
        agents.put("generator", new CodeGeneratorAgent());
        agents.put("react", new ReActAgent(toolManager));
        agents.put("reflecting", new SelfReflectingAgent(toolManager));
        
        System.out.println("✅ 已初始化 " + agents.size() + " 个 Agent");
    }

    public String coordinate(String sessionId, String task) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║  多 Agent 协作系统 - Multi-Agent Coordinator  ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        TaskPlan plan = createTaskPlan(task);
        
        System.out.println("📋 任务分解:");
        System.out.println("  子任务数：" + plan.getSubtasks().size());
        System.out.println("  执行策略：" + plan.getStrategy());
        System.out.println();

        String finalResult = executePlan(sessionId, plan);
        
        executionHistory.add("Task: " + task + " -> Result: " + finalResult.substring(0, Math.min(100, finalResult.length())));
        
        return finalResult;
    }

    private TaskPlan createTaskPlan(String task) {
        TaskPlan plan = new TaskPlan();
        
        String lowerTask = task.toLowerCase();
        
        if (lowerTask.contains("分析") || lowerTask.contains("analyze") || lowerTask.contains("review")) {
            plan.setStrategy("analysis");
            plan.addSubtask(new Subtask("分析代码结构", "analyzer", task));
        } else if (lowerTask.contains("生成") || lowerTask.contains("create") || lowerTask.contains("write")) {
            plan.setStrategy("generation");
            plan.addSubtask(new Subtask("理解需求", "analyzer", task));
            plan.addSubtask(new Subtask("生成代码", "generator", task));
            plan.addSubtask(new Subtask("代码审查", "reflecting", task));
        } else if (lowerTask.contains("重构") || lowerTask.contains("refactor") || lowerTask.contains("优化")) {
            plan.setStrategy("refactoring");
            plan.addSubtask(new Subtask("分析现有代码", "analyzer", task));
            plan.addSubtask(new Subtask("生成优化方案", "react", task));
            plan.addSubtask(new Subtask("实施重构", "generator", task));
        } else if (lowerTask.contains("调试") || lowerTask.contains("debug") || lowerTask.contains("修复")) {
            plan.setStrategy("debugging");
            plan.addSubtask(new Subtask("定位问题", "analyzer", task));
            plan.addSubtask(new Subtask("分析问题", "react", task));
            plan.addSubtask(new Subtask("修复 bug", "generator", task));
        } else {
            plan.setStrategy("general");
            plan.addSubtask(new Subtask("处理任务", "react", task));
        }
        
        return plan;
    }

    private String executePlan(String sessionId, TaskPlan plan) {
        StringBuilder results = new StringBuilder();
        Map<String, String> context = new HashMap<>();
        
        System.out.println("🚀 开始执行任务计划...\n");
        
        for (int i = 0; i < plan.getSubtasks().size(); i++) {
            Subtask subtask = plan.getSubtasks().get(i);
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("步骤 " + (i + 1) + "/" + plan.getSubtasks().size());
            System.out.println("  任务：" + subtask.getDescription());
            System.out.println("  执行 Agent: " + subtask.getAgentName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            BaseAgent agent = agents.get(subtask.getAgentName());
            if (agent == null) {
                System.out.println("❌ 未找到 Agent: " + subtask.getAgentName());
                continue;
            }
            
            String enrichedTask = enrichTask(subtask.getTask(), context);
            String result = agent.process(sessionId, enrichedTask);
            
            context.put("step_" + i, result);
            results.append("步骤 ").append(i + 1).append(" 结果:\n").append(result).append("\n\n");
            
            System.out.println("\n✅ 步骤 " + (i + 1) + " 完成\n");
        }
        
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("✅ 所有步骤执行完毕");
        System.out.println("═══════════════════════════════════════════\n");
        
        return synthesizeResults(plan, results.toString(), context);
    }

    private String enrichTask(String task, Map<String, String> context) {
        if (context.isEmpty()) {
            return task;
        }
        
        StringBuilder enriched = new StringBuilder();
        enriched.append(task).append("\n\n");
        enriched.append("参考上下文:\n");
        
        for (Map.Entry<String, String> entry : context.entrySet()) {
            enriched.append("[").append(entry.getKey()).append("]: ")
                    .append(entry.getValue().substring(0, Math.min(200, entry.getValue().length())))
                    .append("...\n");
        }
        
        return enriched.toString();
    }

    private String synthesizeResults(TaskPlan plan, String rawResults, Map<String, String> context) {
        StringBuilder synthesis = new StringBuilder();
        
        synthesis.append("╔════════════════════════════════════════════╗\n");
        synthesis.append("║         任务执行总结报告                   ║\n");
        synthesis.append("╚════════════════════════════════════════════╝\n\n");
        
        synthesis.append("📋 执行策略：").append(plan.getStrategy()).append("\n");
        synthesis.append("📊 执行步骤：").append(plan.getSubtasks().size()).append(" 步\n\n");
        
        synthesis.append("📝 详细结果:\n");
        synthesis.append(rawResults);
        
        return synthesis.toString();
    }

    public BaseAgent getAgent(String name) {
        return agents.get(name);
    }

    public List<String> getExecutionHistory() {
        return new ArrayList<>(executionHistory);
    }

    public void showAgentStatus() {
        System.out.println("\n=== Agent 状态 ===");
        for (Map.Entry<String, BaseAgent> entry : agents.entrySet()) {
            BaseAgent agent = entry.getValue();
            System.out.println("  " + entry.getKey() + ": " + agent.getName() + " - " + agent.getDescription());
        }
        System.out.println();
    }
}
