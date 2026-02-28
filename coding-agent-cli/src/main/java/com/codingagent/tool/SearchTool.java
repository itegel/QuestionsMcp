package com.codingagent.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SearchTool implements Tool {

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "在指定目录中搜索文件，支持按文件名、扩展名搜索。参数：path (搜索路径), pattern (文件名模式，支持通配符), extension (文件扩展名)";
    }

    @Override
    public String execute(String[] args) {
        Map<String, Object> params = parseArgs(args);
        return search(params);
    }

    @Override
    public Map<String, Object> executeWithMap(Map<String, Object> parameters) {
        String result = search(parameters);
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

    private String search(Map<String, Object> parameters) {
        String pathStr = (String) parameters.getOrDefault("path", ".");
        String pattern = (String) parameters.get("pattern");
        String extension = (String) parameters.get("extension");
        
        int maxResults = 50;
        Object maxResultsObj = parameters.get("maxResults");
        if (maxResultsObj instanceof Number) {
            maxResults = ((Number) maxResultsObj).intValue();
        } else if (maxResultsObj instanceof String) {
            try {
                maxResults = Integer.parseInt((String) maxResultsObj);
            } catch (NumberFormatException ignored) {}
        }

        Path basePath = Paths.get(pathStr);
        if (!Files.exists(basePath)) {
            return "错误：路径不存在：" + pathStr;
        }

        List<String> results = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(basePath)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        if (pattern != null && !pattern.isEmpty()) {
                            String fileName = path.getFileName().toString();
                            return matchesPattern(fileName, pattern);
                        }
                        if (extension != null && !extension.isEmpty()) {
                            return path.getFileName().toString().endsWith("." + extension);
                        }
                        return true;
                    })
                    .limit(maxResults)
                    .forEach(path -> results.add(path.toString()));
        } catch (IOException e) {
            return "搜索出错：" + e.getMessage();
        }

        if (results.isEmpty()) {
            return "未找到匹配的文件";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("找到 ").append(results.size()).append(" 个文件:\n");
        for (String result : results) {
            sb.append("  - ").append(result).append("\n");
        }
        return sb.toString();
    }

    private boolean matchesPattern(String fileName, String pattern) {
        String regex = pattern.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        return fileName.matches(regex);
    }

    @Override
    public String getSchema() {
        return "{\"type\": \"object\", \"properties\": {" +
                "\"path\": {\"type\": \"string\", \"description\": \"搜索路径\"}," +
                "\"pattern\": {\"type\": \"string\", \"description\": \"文件名模式，支持通配符\"}," +
                "\"extension\": {\"type\": \"string\", \"description\": \"文件扩展名\"}," +
                "\"maxResults\": {\"type\": \"integer\", \"description\": \"最大结果数\", \"default\": 50}" +
                "}}";
    }
}
