package com.codingagent.agent.reflect;

import com.codingagent.agent.base.BaseAgent;
import com.codingagent.util.QwenClient;
import com.codingagent.util.QwenRequest;

import java.util.ArrayList;
import java.util.List;

public class ReflectionEngine {

    private final QwenClient qwenClient;

    public ReflectionEngine() {
        this.qwenClient = new QwenClient();
    }

    public ReflectionResult reflect(String taskId, String originalTask, String actionHistory, String result) {
        System.out.println("\n🤔 开始自我反思...");
        
        String reflectionPrompt = buildReflectionPrompt(originalTask, actionHistory, result);
        
        List<QwenRequest.Message> messages = new ArrayList<>();
        messages.add(new QwenRequest.Message("user", reflectionPrompt));
        
        String reflectionOutput = qwenClient.chatWithContext(messages);
        
        ReflectionResult reflectionResult = parseReflection(reflectionOutput);
        reflectionResult.setTaskId(taskId);
        
        System.out.println("✅ 反思完成");
        System.out.println("   质量评分：" + reflectionResult.getQualityScore() + "/10");
        System.out.println("   改进建议：" + reflectionResult.getImprovements().size() + " 条");
        
        return reflectionResult;
    }

    private String buildReflectionPrompt(String task, String actionHistory, String result) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是一个专业的自我反思引擎。请对以下任务执行过程进行深度反思。\n\n");
        
        prompt.append("### 原始任务\n");
        prompt.append(task).append("\n\n");
        
        prompt.append("### 执行历史\n");
        prompt.append(actionHistory).append("\n\n");
        
        prompt.append("### 执行结果\n");
        prompt.append(result).append("\n\n");
        
        prompt.append("### 反思要求\n");
        prompt.append("请从以下维度进行分析：\n");
        prompt.append("1. **效率评估**：行动序列是否最优？有没有冗余步骤？\n");
        prompt.append("2. **准确性评估**：结果是否完全满足任务要求？\n");
        prompt.append("3. **工具使用**：工具选择是否合适？参数是否正确？\n");
        prompt.append("4. **错误处理**：是否妥善处理了异常情况？\n");
        prompt.append("5. **改进建议**：如果重新执行，会如何改进？\n\n");
        
        prompt.append("### 输出格式\n");
        prompt.append("请严格按照以下 JSON 格式输出：\n");
        prompt.append("{\n");
        prompt.append("  \"quality_score\": 8,\n");
        prompt.append("  \"strengths\": [\"优势 1\", \"优势 2\"],\n");
        prompt.append("  \"weaknesses\": [\"不足 1\", \"不足 2\"],\n");
        prompt.append("  \"improvements\": [\"改进建议 1\", \"改进建议 2\"],\n");
        prompt.append("  \"lessons_learned\": \"总结的经验教训\",\n");
        prompt.append("  \"should_retry\": false,\n");
        prompt.append("  \"retry_strategy\": \"如果需要重试，说明重试策略\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    private ReflectionResult parseReflection(String output) {
        ReflectionResult result = new ReflectionResult();
        
        try {
            String json = extractJson(output);
            
            result.setQualityScore(extractNumber(json, "\"quality_score\""));
            result.setStrengths(extractArray(json, "\"strengths\""));
            result.setWeaknesses(extractArray(json, "\"weaknesses\""));
            result.setImprovements(extractArray(json, "\"improvements\""));
            result.setLessonsLearned(extractString(json, "\"lessons_learned\""));
            result.setShouldRetry(extractBoolean(json, "\"should_retry\""));
            result.setRetryStrategy(extractString(json, "\"retry_strategy\""));
            
        } catch (Exception e) {
            System.out.println("⚠️  解析反思结果失败，使用默认值");
            result.setQualityScore(5);
            result.addImprovement("未能解析详细的改进建议");
        }
        
        return result;
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private int extractNumber(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception e) {
        }
        return 5;
    }

    private boolean extractBoolean(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return Boolean.parseBoolean(m.group(1));
            }
        } catch (Exception e) {
        }
        return false;
    }

    private String extractString(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
        }
        return "";
    }

    private java.util.List<String> extractArray(String json, String key) {
        java.util.List<String> list = new ArrayList<>();
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                String arrayContent = m.group(1);
                String[] items = arrayContent.split("\",\"");
                for (String item : items) {
                    String cleaned = item.trim().replace("\"", "");
                    if (!cleaned.isEmpty()) {
                        list.add(cleaned);
                    }
                }
            }
        } catch (Exception e) {
        }
        return list;
    }

    public void saveReflection(String taskId, ReflectionResult reflection) {
        System.out.println("💾 保存反思结果：" + taskId);
        ReflectionHistory.getInstance().addReflection(taskId, reflection);
    }

    public ReflectionResult getReflection(String taskId) {
        return ReflectionHistory.getInstance().getReflection(taskId);
    }
}
