package com.codingagent.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GitTool implements Tool {

    @Override
    public String getName() {
        return "git";
    }

    @Override
    public String getDescription() {
        return "执行 Git 命令。参数：command (git 命令，如 status, log, diff 等), path (可选，仓库路径，默认为当前目录)";
    }

    @Override
    public String execute(String[] args) {
        Map<String, Object> params = parseArgs(args);
        return executeGit(params);
    }

    @Override
    public Map<String, Object> executeWithMap(Map<String, Object> parameters) {
        String result = executeGit(parameters);
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

    private String executeGit(Map<String, Object> parameters) {
        String command = (String) parameters.get("command");
        String path = (String) parameters.getOrDefault("path", ".");

        if (command == null || command.isEmpty()) {
            return "错误：需要指定 command 参数";
        }

        try {
            String[] cmdArray = new String[command.split("\\s+").length + 1];
            cmdArray[0] = "git";
            String[] parts = command.split("\\s+");
            System.arraycopy(parts, 0, cmdArray, 1, parts.length);
            
            ProcessBuilder pb = new ProcessBuilder(cmdArray);
            pb.directory(new java.io.File(path));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroy();
                return "错误：Git 命令执行超时";
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return "Git 命令执行失败 (退出码：" + exitCode + "): " + output.toString();
            }

            String result = output.toString();
            if (result.isEmpty()) {
                return "命令执行成功，无输出";
            }
            return result;
        } catch (IOException e) {
            return "执行 Git 命令失败：" + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Git 命令被中断";
        }
    }

    @Override
    public String getSchema() {
        return "{\"type\": \"object\", \"properties\": {" +
                "\"command\": {\"type\": \"string\", \"description\": \"Git 命令，如 status, log, diff 等\"}," +
                "\"path\": {\"type\": \"string\", \"description\": \"仓库路径，默认为当前目录\"}" +
                "}, \"required\": [\"command\"]}";
    }
}
