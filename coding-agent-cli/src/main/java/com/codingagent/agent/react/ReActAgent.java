package com.codingagent.agent.react;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.tool.ToolManager;
import com.codingagent.util.QwenRequest;

import java.util.List;

public class ReActAgent extends BaseAgent {

    private final ActionOrchestrator orchestrator;
    private final ThoughtParser thoughtParser;
    private final int maxIterations;

    public ReActAgent(ToolManager toolManager) {
        this(toolManager, 10);
    }

    public ReActAgent(ToolManager toolManager, int maxIterations) {
        super("ReActAgent", "智能推理 Agent");
        this.orchestrator = new ActionOrchestrator(toolManager);
        this.thoughtParser = new ThoughtParser();
        this.maxIterations = maxIterations;
    }

    @Override
    public String process(String sessionId, String task) {
        ReActState state = new ReActState(sessionId, task, maxIterations);
        
        System.out.println("\n=== 开始 ReAct 推理过程 ===");
        System.out.println("任务：" + task);
        System.out.println("最大迭代次数：" + maxIterations);
        System.out.println("========================\n");

        while (state.shouldContinue()) {
            state.incrementIteration();
            System.out.println("\n--- 迭代 " + state.getIterationCount() + " ---");

            String prompt = buildPrompt(state);
            
            // 创建可变的消息列表
            List<QwenRequest.Message> messages = new java.util.ArrayList<>(memoryService.getMessages(sessionId));
            messages.add(new QwenRequest.Message("user", prompt));
            
            String llmOutput = qwenClient.chatWithContext(messages);
            
            System.out.println("\n📝 LLM 原始输出:");
            System.out.println("---");
            System.out.println(llmOutput);
            System.out.println("---\n");
            
            ThoughtParser.ParseResult parseResult = thoughtParser.parse(llmOutput);
            
            System.out.println("🔍 解析结果:");
            System.out.println("  Thought: " + (parseResult.getThought() != null ? "✓" : "✗"));
            System.out.println("  Action: " + (parseResult.getAction() != null ? "✓" : "✗"));
            System.out.println("  FinalAnswer: " + (parseResult.getFinalAnswer() != null ? "✓" : "✗"));
            
            if (parseResult.getThought() != null) {
                state.addThought(parseResult.getThought());
                System.out.println("思考：" + parseResult.getThought().getContent());
            }

            if (parseResult.hasFinalAnswer()) {
                state.setFinalAnswer(parseResult.getFinalAnswer());
                System.out.println("\n最终答案：" + parseResult.getFinalAnswer());
                break;
            }

            if (parseResult.hasAction()) {
                Action action = parseResult.getAction();
                state.addAction(action);
                System.out.println("行动：" + action);

                String observation = orchestrator.execute(action);
                state.addObservation(observation);
                System.out.println("观察：" + observation);

                memoryService.addMessage(sessionId, "assistant", llmOutput);
                memoryService.addMessage(sessionId, "user", "Observation: " + observation);
            } else if (!parseResult.hasFinalAnswer()) {
                System.out.println("警告：LLM 输出未包含有效的行动或最终答案");
                memoryService.addMessage(sessionId, "assistant", llmOutput);
                memoryService.addMessage(sessionId, "user", 
                    "请按照格式提供思考、行动或最终答案。可用工具：" + orchestrator.getAvailableTools());
            }
        }

        if (state.getFinalAnswer() == null) {
            state.setFinalAnswer("抱歉，未能在 " + maxIterations + " 次迭代内完成任务。" +
                               "已执行的思考：" + state.getThoughts().size() + 
                               ", 已执行的行动：" + state.getActions().size());
        }

        memoryService.addMessage(sessionId, "assistant", "Final Answer: " + state.getFinalAnswer());
        
        System.out.println("\n=== ReAct 推理过程结束 ===\n");
        return state.getFinalAnswer();
    }

    private String buildPrompt(ReActState state) {
        StringBuilder prompt = new StringBuilder();
        
        String systemPrompt = ThoughtParser.buildSystemPrompt()
                .replace("{tool_descriptions}", orchestrator.getAvailableTools())
                .replace("{max_iterations}", String.valueOf(maxIterations));
        
        prompt.append(systemPrompt).append("\n");
        prompt.append("当前任务：").append(state.getTask()).append("\n\n");

        if (!state.getThoughts().isEmpty() || !state.getObservations().isEmpty()) {
            prompt.append("=== 历史对话 ===\n");
            prompt.append(state.buildContext());
        }

        prompt.append("\n请开始你的思考：\n");
        return prompt.toString();
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public ActionOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
