package com.codingagent.agent.reflect;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.agent.react.ReActAgent;
import com.codingagent.tool.ToolManager;

import java.util.UUID;

public class SelfReflectingAgent extends BaseAgent {

    private final ReActAgent reactAgent;
    private final ReflectionEngine reflectionEngine;

    public SelfReflectingAgent(ToolManager toolManager) {
        super("SelfReflectingAgent", "带自我反思的智能 Agent");
        this.reactAgent = new ReActAgent(toolManager);
        this.reflectionEngine = new ReflectionEngine();
    }

    @Override
    public String process(String sessionId, String task) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║  自我反思 Agent - Self-Reflective Agent      ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        String taskId = UUID.randomUUID().toString();
        
        System.out.println("🚀 第一阶段：执行任务");
        System.out.println("任务：" + task);
        System.out.println();

        String result = reactAgent.process(sessionId, task);

        System.out.println("\n📊 初步结果：" + result.substring(0, Math.min(200, result.length())) + "...");

        System.out.println("\n🤔 第二阶段：自我反思");
        
        String actionHistory = buildActionHistory(sessionId);
        ReflectionResult reflection = reflectionEngine.reflect(taskId, task, actionHistory, result);

        System.out.println("\n" + reflection);

        if (reflection.isShouldRetry() && reflection.getQualityScore() < 7) {
            System.out.println("\n🔄 第三阶段：根据反思重新执行");
            System.out.println("重试策略：" + reflection.getRetryStrategy());

            String refinedTask = refineTask(task, reflection);
            System.out.println("优化后的任务：" + refinedTask);

            result = reactAgent.process(sessionId, refinedTask);
            
            System.out.println("\n✅ 最终结果：" + result);
        } else {
            System.out.println("\n✅ 结果质量良好，无需重试");
        }

        reflectionEngine.saveReflection(taskId, reflection);

        return result;
    }

    private String buildActionHistory(String sessionId) {
        StringBuilder history = new StringBuilder();
        history.append("会话 ").append(sessionId).append(" 的执行历史:\n");
        
        java.util.List<com.codingagent.util.QwenRequest.Message> messages = memoryService.getMessages(sessionId);
        for (com.codingagent.util.QwenRequest.Message msg : messages) {
            String role = msg.getRole();
            String content = msg.getContent();
            if (content.length() > 300) {
                content = content.substring(0, 300) + "...";
            }
            history.append("[").append(role).append("]: ").append(content).append("\n");
        }
        
        return history.toString();
    }

    private String refineTask(String originalTask, ReflectionResult reflection) {
        StringBuilder refined = new StringBuilder();
        refined.append(originalTask).append("\n\n");
        refined.append("请特别注意以下改进点：\n");
        
        for (String improvement : reflection.getImprovements()) {
            refined.append("- ").append(improvement).append("\n");
        }
        
        if (!reflection.getWeaknesses().isEmpty()) {
            refined.append("\n避免以下问题：\n");
            for (String weakness : reflection.getWeaknesses()) {
                refined.append("- ").append(weakness).append("\n");
            }
        }
        
        return refined.toString();
    }

    public ReflectionResult getLastReflection() {
        return ReflectionHistory.getInstance().getLatestReflection();
    }
}
