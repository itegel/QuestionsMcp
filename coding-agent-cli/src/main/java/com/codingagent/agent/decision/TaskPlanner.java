package com.codingagent.agent.decision;

import com.codingagent.util.QwenClient;
import com.codingagent.util.QwenRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskPlanner {

    private final QwenClient qwenClient;

    public static class SubTask {
        private String title;
        private String description;
        private String type;
        private Map<String, Object> parameters;
        private boolean completed;

        public SubTask(String title, String description, String type, Map<String, Object> parameters) {
            this.title = title;
            this.description = description;
            this.type = type;
            this.parameters = parameters;
            this.completed = false;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        @Override
        public String toString() {
            return title + " - " + description;
        }
    }

    public TaskPlanner() {
        this.qwenClient = new QwenClient();
    }

    public List<SubTask> decompose(String goal, IntentRecognizer.Intent intent) {
        String prompt = buildPrompt(goal, intent);
        
        List<QwenRequest.Message> messages = new ArrayList<>();
        messages.add(new QwenRequest.Message("user", prompt));
        
        String response = qwenClient.chatWithContext(messages);
        
        return parseSubTasks(response);
    }

    private String buildPrompt(String goal, IntentRecognizer.Intent intent) {
        return "你是一个任务规划助手。请将用户的复杂目标拆解成可执行的小任务。\n\n" +
                "用户目标：" + goal + "\n" +
                "意图类型：" + intent.getType().getName() + "\n" +
                "意图描述：" + intent.getReasoning() + "\n\n" +
                "可用的任务类型：\n" +
                "- read_file: 读取文件内容\n" +
                "- write_file: 写入文件\n" +
                "- analyze: 分析代码\n" +
                "- search: 搜索文件\n" +
                "- execute: 执行命令\n" +
                "- review: 审查代码\n" +
                "- generate: 生成代码\n" +
                "- test: 运行测试\n\n" +
                "请按照以下 JSON 格式返回任务列表：\n" +
                "[\n" +
                "  {\n" +
                "    \"title\": \"任务标题\",\n" +
                "    \"description\": \"任务详细描述\",\n" +
                "    \"type\": \"任务类型\",\n" +
                "    \"parameters\": {\n" +
                "      \"key\": \"value\"\n" +
                "    }\n" +
                "  }\n" +
                "]\n\n" +
                "只返回 JSON 数组，不要有其他内容。任务应该有逻辑顺序，从简单到复杂。";
    }

    private List<SubTask> parseSubTasks(String response) {
        List<SubTask> tasks = new ArrayList<>();
        
        try {
            response = response.trim();
            if (response.startsWith("```json")) {
                response = response.substring(7);
            }
            if (response.endsWith("```")) {
                response = response.substring(0, response.length() - 3);
            }
            response = response.trim();
            
            int start = response.indexOf('[');
            int end = response.lastIndexOf(']');
            
            if (start == -1 || end == -1) {
                return createDefaultTasks(response);
            }
            
            String arrayContent = response.substring(start, end + 1);
            
            List<Map<String, Object>> taskMaps = parseJsonArray(arrayContent);
            
            for (Map<String, Object> taskMap : taskMaps) {
                String title = (String) taskMap.getOrDefault("title", "未命名任务");
                String description = (String) taskMap.getOrDefault("description", "");
                String type = (String) taskMap.getOrDefault("type", "analyze");
                
                @SuppressWarnings("unchecked")
                Map<String, Object> parameters = (Map<String, Object>) taskMap.get("parameters");
                if (parameters == null) {
                    parameters = new HashMap<>();
                }
                
                tasks.add(new SubTask(title, description, type, parameters));
            }
        } catch (Exception e) {
            return createDefaultTasks("解析失败：" + e.getMessage());
        }
        
        if (tasks.isEmpty()) {
            return createDefaultTasks(response);
        }
        
        return tasks;
    }

    private List<SubTask> createDefaultTasks(String fallback) {
        List<SubTask> tasks = new ArrayList<>();
        tasks.add(new SubTask(
            "处理任务",
            fallback,
            "analyze",
            new HashMap<>()
        ));
        return tasks;
    }

    private List<Map<String, Object>> parseJsonArray(String arrayStr) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        int depth = 0;
        int start = -1;
        
        for (int i = 0; i < arrayStr.length(); i++) {
            char c = arrayStr.charAt(i);
            
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    String objStr = arrayStr.substring(start, i + 1);
                    result.add(parseJsonObject(objStr));
                    start = -1;
                }
            }
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonObject(String objStr) {
        Map<String, Object> result = new HashMap<>();
        
        objStr = objStr.trim();
        if (objStr.startsWith("{") && objStr.endsWith("}")) {
            objStr = objStr.substring(1, objStr.length() - 1);
        }
        
        String[] pairs = objStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim();
                
                if (value.startsWith("{")) {
                    result.put(key, parseJsonObject(value));
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    result.put(key, value.substring(1, value.length() - 1));
                } else {
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }

    public String generateTaskSummary(List<SubTask> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("任务计划 (共 ").append(tasks.size()).append(" 步):\n\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            SubTask task = tasks.get(i);
            sb.append(i + 1).append(". ").append(task.getTitle()).append("\n");
            sb.append("   ").append(task.getDescription()).append("\n");
            sb.append("   类型：").append(task.getType()).append("\n\n");
        }
        
        return sb.toString();
    }
}
