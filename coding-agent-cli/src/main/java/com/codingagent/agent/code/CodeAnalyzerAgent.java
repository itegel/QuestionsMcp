package com.codingagent.agent.code;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.tool.ToolManager;
import com.codingagent.util.QwenRequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CodeAnalyzerAgent extends BaseAgent {

    public CodeAnalyzerAgent() {
        super("CodeAnalyzer", "代码分析专家");
    }

    @Override
    public String process(String sessionId, String task) {
        String filePath = extractFilePath(task);
        String codeContent = "";
        
        if (filePath != null && !filePath.isEmpty()) {
            System.out.println("\n📂 正在读取文件：" + filePath);
            codeContent = readFileContent(filePath);
            
            // 检查是否读取失败
            if (codeContent.startsWith("错误") || codeContent.startsWith("Error") || 
                codeContent.startsWith("读取文件出错") || codeContent.contains("No such file")) {
                System.out.println("❌ 文件读取失败：" + filePath);
                // 直接返回错误信息，不调用 LLM
                return "❌ 无法读取文件：" + filePath + "\n\n原因：" + codeContent + 
                       "\n\n请检查：\n1. 文件路径是否正确\n2. 文件是否存在\n3. 是否有读取权限";
            }
            System.out.println("✅ 文件读取成功，共 " + codeContent.length() + " 字符");
        } else {
            List<QwenRequest.Message> existingMessages = memoryService.getMessages(sessionId);
            if (existingMessages != null && !existingMessages.isEmpty()) {
                System.out.println("⚠️  未能从请求中提取文件路径，将基于历史对话上下文进行分析。");
            } else {
                System.out.println("⚠️  未能从请求中提取文件路径，且无历史对话");
                return "⚠️  请提供完整的文件路径，例如：\n\"帮我分析 src/main/java/com/codingagent/tool/ToolManager.java\"";
            }
        }
        
        String prompt = buildAnalysisPrompt(task, codeContent);
        
        memoryService.addMessage(sessionId, "user", prompt);
        List<QwenRequest.Message> messages = memoryService.getMessages(sessionId);
        
        String response = qwenClient.chatWithContext(messages);
        
        memoryService.addMessage(sessionId, "assistant", response);
        
        return response;
    }
    
    private String extractFilePath(String task) {
        // 清理常见的前缀词
        String[] prefixes = {"帮我分析", "分析", "查看", "读取", "打开", "请分析", "请查看"};
        String cleanedTask = task;
        for (String prefix : prefixes) {
            if (task.startsWith(prefix)) {
                cleanedTask = task.substring(prefix.length()).trim();
                break;
            }
        }
        
        // 提取文件路径
        String[] indicators = {"src/", "java/", ".java", ".py", ".js", ".ts", ".cpp", ".c", ".h"};
        for (String indicator : indicators) {
            int index = cleanedTask.indexOf(indicator);
            if (index != -1) {
                int start = index;
                while (start > 0 && cleanedTask.charAt(start - 1) != ' ' && cleanedTask.charAt(start - 1) != '"' && cleanedTask.charAt(start - 1) != '\'') {
                    start--;
                }
                int end = cleanedTask.length();
                for (int i = index; i < cleanedTask.length(); i++) {
                    if (cleanedTask.charAt(i) == ' ' || cleanedTask.charAt(i) == '"' || cleanedTask.charAt(i) == '\'' || cleanedTask.charAt(i) == '，' || cleanedTask.charAt(i) == ',') {
                        end = i;
                        break;
                    }
                }
                String path = cleanedTask.substring(start, end).trim();
                System.out.println("🔍 提取的文件路径：" + path);
                return path;
            }
        }
        return null;
    }
    
    private String readFileContent(String filePath) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "read");
            
            // 使用绝对路径
            String absolutePath = new java.io.File(filePath).getAbsolutePath();
            params.put("path", absolutePath);
            
            System.out.println("📖 读取文件：" + absolutePath);
            Map<String, Object> result = toolManager.executeToolWithMap("file", params);
            String resultStr = result.getOrDefault("result", "读取失败").toString();
            System.out.println("📄 文件大小：" + resultStr.length() + " 字符");
            return resultStr;
        } catch (Exception e) {
            return "读取文件出错：" + e.getMessage();
        }
    }
    
    private String buildAnalysisPrompt(String task, String codeContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的代码分析专家。\n\n");
        prompt.append("用户请求：").append(task).append("\n\n");
        
        if (!codeContent.isEmpty()) {
            prompt.append("=== 代码内容 ===\n");
            prompt.append(codeContent).append("\n");
            prompt.append("===============\n\n");
        }
        
        prompt.append("请从以下角度进行分析：\n");
        prompt.append("1. 代码结构和组织\n");
        prompt.append("2. 主要功能和职责\n");
        prompt.append("3. 设计模式和最佳实践的使用\n");
        prompt.append("4. 潜在问题和改进建议\n");
        prompt.append("5. 代码质量和可维护性\n\n");
        prompt.append("请用清晰、专业的方式回答。");
        
        return prompt.toString();
    }
}
