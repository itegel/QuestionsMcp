package com.codingagent.agent.react;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThoughtParser {

    private static final Pattern THOUGHT_PATTERN = Pattern.compile(
            "(?:Thought|思考)[:：]\\s*(.+?)(?=\\n|$)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    private static final Pattern ACTION_PATTERN = Pattern.compile(
            "(?:Action|行动)[:：]\\s*([\\w]+)(?:\\s*\\|\\s*(.+?))?(?=\\n|Thought|思考|Action|行动|Observation|观察|Final|最终|$)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    private static final Pattern OBSERVATION_PATTERN = Pattern.compile(
            "(?:Observation|观察)[:：]\\s*(.+?)(?=\\n|$|Thought|思考|Action|行动|Final|最终)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    private static final Pattern FINAL_ANSWER_PATTERN = Pattern.compile(
            "(?:Final Answer|最终答案)[:：]\\s*(.+?)(?=\\n|$)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public static class ParseResult {
        private Thought thought;
        private Action action;
        private String observation;
        private String finalAnswer;

        public ParseResult(Thought thought, Action action, String observation, String finalAnswer) {
            this.thought = thought;
            this.action = action;
            this.observation = observation;
            this.finalAnswer = finalAnswer;
        }

        public Thought getThought() {
            return thought;
        }

        public Action getAction() {
            return action;
        }

        public String getObservation() {
            return observation;
        }

        public String getFinalAnswer() {
            return finalAnswer;
        }

        public boolean hasAction() {
            return action != null;
        }

        public boolean hasFinalAnswer() {
            return finalAnswer != null;
        }
    }

    public ParseResult parse(String llmOutput) {
        if (llmOutput == null || llmOutput.trim().isEmpty()) {
            return new ParseResult(null, null, null, null);
        }

        Thought thought = parseThought(llmOutput);
        Action action = parseAction(llmOutput);
        String observation = parseObservation(llmOutput);
        String finalAnswer = parseFinalAnswer(llmOutput);

        return new ParseResult(thought, action, observation, finalAnswer);
    }

    private Thought parseThought(String text) {
        Matcher matcher = THOUGHT_PATTERN.matcher(text);
        if (matcher.find()) {
            String content = matcher.group(1).trim();
            return new Thought(content, Thought.ThoughtType.REASONING);
        }
        return null;
    }

    private Action parseAction(String text) {
        Matcher matcher = ACTION_PATTERN.matcher(text);
        if (matcher.find()) {
            String toolName = matcher.group(1).trim();
            String paramsStr = matcher.group(2);
            
            Map<String, Object> parameters = new HashMap<>();
            if (paramsStr != null && !paramsStr.trim().isEmpty()) {
                parameters = parseParameters(paramsStr.trim());
            }
            
            return new Action(toolName, parameters);
        }
        return null;
    }

    private Map<String, Object> parseParameters(String paramsStr) {
        Map<String, Object> parameters = new HashMap<>();
        
        if (paramsStr == null || paramsStr.trim().isEmpty()) {
            return parameters;
        }
        
        String trimmed = paramsStr.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(trimmed, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
            }
        }
        
        // 尝试用逗号或空格分割 (仅当不是合法JSON时才作为回退)
        String[] pairs;
        if (trimmed.contains(",")) {
            pairs = trimmed.split(",");
        } else {
            pairs = trimmed.split("\\s+");
        }
        
        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) {
                continue;
            }
            
            String[] keyValue = pair.split("[:=]", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("[\"']", "");
                String value = keyValue[1].trim().replaceAll("[\"']", "");
                if (!key.isEmpty() && !value.isEmpty()) {
                    parameters.put(key, value);
                }
            } else if (keyValue.length == 1 && !keyValue[0].trim().isEmpty()) {
                // 处理只有值没有键的情况（可能是位置参数）
                parameters.put("arg" + parameters.size(), keyValue[0].trim().replaceAll("[\"']", ""));
            }
        }
        
        return parameters;
    }

    private String parseObservation(String text) {
        Matcher matcher = OBSERVATION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String parseFinalAnswer(String text) {
        Matcher matcher = FINAL_ANSWER_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    public static String buildSystemPrompt() {
        return "你是一个智能编程助手，具有代码分析、生成、审查等能力。你可以使用以下工具来完成任务：\n" +
                "{tool_descriptions}\n\n" +
                "请按照以下格式思考和行动：\n\n" +
                "Thought: 思考当前需要做什么，分析任务和已有信息\n" +
                "Action: 工具名称 | 参数（JSON 格式或 key=value 形式）\n" +
                "Observation: 工具执行的结果（由系统提供）\n" +
                "...\n" +
                "Final Answer: 给用户的最终答案\n\n" +
                "示例：\n" +
                "Thought: 用户想要分析这个文件，我需要先读取文件内容\n" +
                "Action: file | action=read, path=src/main/java/App.java\n" +
                "Observation: 文件内容：...\n" +
                "Thought: 现在我已经看到了代码，可以进行分析了\n" +
                "Final Answer: 这段代码的主要功能是...\n\n" +
                "记住：\n" +
                "1. 每次只执行一个行动\n" +
                "2. 根据观察结果决定下一步\n" +
                "3. 当有足够信息时，给出最终答案\n" +
                "4. 最多进行 {max_iterations} 次迭代\n";
    }
}
