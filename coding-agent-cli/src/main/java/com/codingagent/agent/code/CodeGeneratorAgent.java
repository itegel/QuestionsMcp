package com.codingagent.agent.code;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.util.QwenRequest;

import java.util.List;

public class CodeGeneratorAgent extends BaseAgent {

    public CodeGeneratorAgent() {
        super("CodeGenerator", "代码生成专家");
    }

    @Override
    public String process(String sessionId, String task) {
        // 构建生成提示
        String prompt = "请根据以下需求生成代码，确保代码正确、高效且符合最佳实践：\n" + task;
        
        // 添加用户消息到记忆
        memoryService.addMessage(sessionId, "user", prompt);
        
        // 获取上下文消息
        List<QwenRequest.Message> messages = memoryService.getMessages(sessionId);
        
        // 调用 LLM 生成代码
        String response = qwenClient.chatWithContext(messages);
        
        // 添加 AI 响应到记忆
        memoryService.addMessage(sessionId, "assistant", response);
        
        return response;
    }

}
