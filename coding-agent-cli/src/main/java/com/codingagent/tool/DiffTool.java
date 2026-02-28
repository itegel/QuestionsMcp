package com.codingagent.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class DiffTool implements Tool {

    @Override
    public String getName() {
        return "diff";
    }

    @Override
    public String getDescription() {
        return "比较两个文件的差异。参数：file1 (第一个文件路径), file2 (第二个文件路径)";
    }

    @Override
    public String execute(String[] args) {
        Map<String, Object> params = parseArgs(args);
        return diff(params);
    }

    @Override
    public Map<String, Object> executeWithMap(Map<String, Object> parameters) {
        String result = diff(parameters);
        Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("result", result);
        return resultMap;
    }

    private Map<String, Object> parseArgs(String[] args) {
        Map<String, Object> params = new java.util.HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                params.put(args[i], args[i + 1]);
            }
        }
        return params;
    }

    private String diff(Map<String, Object> parameters) {
        String file1 = (String) parameters.get("file1");
        String file2 = (String) parameters.get("file2");

        if (file1 == null || file2 == null) {
            return "错误：需要指定 file1 和 file2 参数";
        }

        try {
            String content1 = new String(Files.readAllBytes(Paths.get(file1)));
            String content2 = new String(Files.readAllBytes(Paths.get(file2)));

            String[] lines1 = content1.split("\n");
            String[] lines2 = content2.split("\n");

            StringBuilder sb = new StringBuilder();
            sb.append("文件差异比较:\n");
            sb.append("文件 1: ").append(file1).append("\n");
            sb.append("文件 2: ").append(file2).append("\n\n");

            int maxLen = Math.max(lines1.length, lines2.length);
            boolean hasDiff = false;

            for (int i = 0; i < maxLen; i++) {
                String line1 = i < lines1.length ? lines1[i] : "";
                String line2 = i < lines2.length ? lines2[i] : "";

                if (!line1.equals(line2)) {
                    hasDiff = true;
                    sb.append("行 ").append(i + 1).append(":\n");
                    if (i < lines1.length) {
                        sb.append("  - ").append(line1).append("\n");
                    }
                    if (i < lines2.length) {
                        sb.append("  + ").append(line2).append("\n");
                    }
                    sb.append("\n");
                }
            }

            if (!hasDiff) {
                return "两个文件内容完全相同";
            }

            return sb.toString();
        } catch (IOException e) {
            return "读取文件失败：" + e.getMessage();
        }
    }

    @Override
    public String getSchema() {
        return "{\"type\": \"object\", \"properties\": {" +
                "\"file1\": {\"type\": \"string\", \"description\": \"第一个文件路径\"}," +
                "\"file2\": {\"type\": \"string\", \"description\": \"第二个文件路径\"}" +
                "}, \"required\": [\"file1\", \"file2\"]}";
    }
}
