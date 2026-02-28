package com.codingagent.agent.decision;

import com.codingagent.util.QwenClient;
import com.codingagent.util.QwenRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntentRecognizer {

    private final QwenClient qwenClient;

    public enum IntentType {
        ANALYZE("分析", "分析代码结构、功能、问题"),
        GENERATE("生成", "创建新代码或文件"),
        REVIEW("审查", "审查代码质量和安全性"),
        REFACTOR("重构", "改进现有代码结构"),
        DEBUG("调试", "查找和修复 bug"),
        EXPLAIN("解释", "解释代码功能"),
        TEST("测试", "生成测试或运行测试"),
        SEARCH("搜索", "搜索代码或文件"),
        CHAT("聊天", "一般对话或咨询");

        private final String name;
        private final String description;

        IntentType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name + " - " + description;
        }
    }

    public static class Intent {
        private IntentType type;
        private Map<String, Object> parameters;
        private String confidence;
        private String reasoning;

        public Intent(IntentType type, Map<String, Object> parameters, String confidence, String reasoning) {
            this.type = type;
            this.parameters = parameters;
            this.confidence = confidence;
            this.reasoning = reasoning;
        }

        public IntentType getType() {
            return type;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public String getConfidence() {
            return confidence;
        }

        public String getReasoning() {
            return reasoning;
        }

        public String getFileParameter() {
            if (parameters.containsKey("file") || parameters.containsKey("path")) {
                return (String) parameters.getOrDefault("file", parameters.get("path"));
            }
            return null;
        }
    }

    public IntentRecognizer() {
        this.qwenClient = new QwenClient();
    }

    public Intent recognize(String userInput) {
        String prompt = buildPrompt(userInput);
        
        List<QwenRequest.Message> messages = new ArrayList<>();
        messages.add(new QwenRequest.Message("user", prompt));
        
        String response = qwenClient.chatWithContext(messages);
        
        return parseIntent(response, userInput);
    }

    private String buildPrompt(String userInput) {
        return "你是一个意图识别助手。请分析用户的输入，判断其意图类型，并提取相关参数。\n\n" +
                "可用的意图类型：\n" +
                "1. ANALYZE - 分析代码结构、功能、问题\n" +
                "2. GENERATE - 创建新代码或文件\n" +
                "3. REVIEW - 审查代码质量和安全性\n" +
                "4. REFACTOR - 改进现有代码结构\n" +
                "5. DEBUG - 查找和修复 bug\n" +
                "6. EXPLAIN - 解释代码功能\n" +
                "7. TEST - 生成测试或运行测试\n" +
                "8. SEARCH - 搜索代码或文件\n" +
                "9. CHAT - 一般对话或咨询\n\n" +
                "用户输入：" + userInput + "\n\n" +
                "请按照以下 JSON 格式返回：\n" +
                "{\n" +
                "  \"intent\": \"意图类型（英文）\",\n" +
                "  \"confidence\": \"高/中/低\",\n" +
                "  \"reasoning\": \"判断理由\",\n" +
                "  \"parameters\": {\n" +
                "    \"file\": \"文件路径（如果有）\",\n" +
                "    \"language\": \"编程语言（如果能推断）\",\n" +
                "    \"description\": \"具体需求描述\"\n" +
                "  }\n" +
                "}\n\n" +
                "只返回 JSON，不要有其他内容。";
    }

    private Intent parseIntent(String response, String userInput) {
        try {
            Map<String, Object> json = parseJson(response);
            
            String intentStr = (String) json.get("intent");
            IntentType intentType = parseIntentType(intentStr);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) json.get("parameters");
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            
            String confidence = (String) json.get("confidence");
            String reasoning = (String) json.get("reasoning");
            
            if (!parameters.containsKey("description")) {
                parameters.put("description", userInput);
            }
            
            return new Intent(intentType, parameters, confidence, reasoning);
        } catch (Exception e) {
            return new Intent(IntentType.CHAT, 
                java.util.Collections.singletonMap("description", userInput),
                "低", 
                "解析失败：" + e.getMessage());
        }
    }

    private IntentType parseIntentType(String intentStr) {
        if (intentStr == null) {
            return IntentType.CHAT;
        }
        
        String upper = intentStr.toUpperCase();
        for (IntentType type : IntentType.values()) {
            if (type.name().equals(upper) || type.getName().equals(intentStr)) {
                return type;
            }
        }
        
        if (upper.contains("分析") || upper.contains("ANALYZE")) {
            return IntentType.ANALYZE;
        } else if (upper.contains("生成") || upper.contains("CREATE") || upper.contains("GENERATE")) {
            return IntentType.GENERATE;
        } else if (upper.contains("审查") || upper.contains("REVIEW")) {
            return IntentType.REVIEW;
        } else if (upper.contains("重构") || upper.contains("REFACTOR")) {
            return IntentType.REFACTOR;
        } else if (upper.contains("调试") || upper.contains("DEBUG")) {
            return IntentType.DEBUG;
        } else if (upper.contains("解释") || upper.contains("EXPLAIN")) {
            return IntentType.EXPLAIN;
        } else if (upper.contains("测试") || upper.contains("TEST")) {
            return IntentType.TEST;
        } else if (upper.contains("搜索") || upper.contains("SEARCH")) {
            return IntentType.SEARCH;
        }
        
        return IntentType.CHAT;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String jsonStr) {
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("```json")) {
            jsonStr = jsonStr.substring(7);
        }
        if (jsonStr.endsWith("```")) {
            jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
        }
        jsonStr = jsonStr.trim();
        
        return parseJsonMap(jsonStr);
    }

    private Map<String, Object> parseJsonMap(String jsonStr) {
        Map<String, Object> result = new HashMap<>();
        
        int braceCount = 0;
        boolean inString = false;
        boolean escape = false;
        
        for (char c : jsonStr.toCharArray()) {
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (!inString) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
            }
        }
        
        if (braceCount != 0) {
            for (int i = 0; i < Math.max(0, braceCount); i++) {
                jsonStr += "}";
            }
        }
        
        jsonStr = jsonStr.replaceAll("[\\n\\r]", " ").replaceAll("\\s+", " ");
        
        int start = jsonStr.indexOf('{');
        int end = jsonStr.lastIndexOf('}');
        
        if (start == -1 || end == -1) {
            return result;
        }
        
        String content = jsonStr.substring(start + 1, end).trim();
        
        String[] pairs = content.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim();
                
                if (value.startsWith("{")) {
                    result.put(key, parseJsonMap(value));
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    result.put(key, value.substring(1, value.length() - 1));
                } else {
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
}
