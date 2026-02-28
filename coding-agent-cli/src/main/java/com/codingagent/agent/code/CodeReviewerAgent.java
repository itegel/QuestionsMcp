package com.codingagent.agent.code;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.util.QwenRequest;

import java.util.List;

public class CodeReviewerAgent extends BaseAgent {

    public CodeReviewerAgent() {
        super("CodeReviewer", "代码审查专家");
    }

    @Override
    public String process(String sessionId, String task) {
        // 构建审查提示
        String prompt = "请审查以下代码，检查其质量、安全性、可读性和性能，并提供改进建议：\n" + task;
        
        // 添加用户消息到记忆
        memoryService.addMessage(sessionId, "user", prompt);
        
        // 获取上下文消息
        List<QwenRequest.Message> messages = memoryService.getMessages(sessionId);
        
        // 调用 LLM 进行审查
        String response = qwenClient.chatWithContext(messages);
        
        // 添加 AI 响应到记忆
        memoryService.addMessage(sessionId, "assistant", response);
        
        return response;
    }

}
