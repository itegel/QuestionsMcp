package com.codingagent.tool;

import com.codingagent.util.ConfigLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileTool implements Tool {

    private final String basePath;

    public FileTool() {
        this.basePath = ConfigLoader.getProperty("tool.file.base-path", ".");
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getDescription() {
        return "File operation tool: read, write, list files. Usage: file [read|write|list] [path] [content]";
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 2) {
            return "Insufficient arguments. Usage: file [read|write|list] [path] [content]";
        }

        String operation = args[1];
        String path = args[2];
        
        // 支持绝对路径和相对路径
        Path fullPath;
        if (path.startsWith("/") || path.matches("^[a-zA-Z]:\\\\.*")) {
            fullPath = Paths.get(path);
        } else {
            fullPath = Paths.get(basePath, path);
        }

        try {
            switch (operation) {
                case "read":
                    return readFile(fullPath);
                case "write":
                    if (args.length < 4) {
                        return "Insufficient arguments for write operation. Usage: file write [path] [content]";
                    }
                    StringBuilder content = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        content.append(args[i]).append(" ");
                    }
                    return writeFile(fullPath, content.toString().trim());
                case "list":
                    return listFiles(fullPath);
                default:
                    return "Unknown operation: " + operation;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public java.util.Map<String, Object> executeWithMap(java.util.Map<String, Object> parameters) {
        String operation = (String) parameters.getOrDefault("operation", parameters.get("action"));
        if (operation == null) {
            return java.util.Collections.singletonMap("error", "Missing operation parameter");
        }
        
        String pathStr = (String) parameters.get("path");
        if (pathStr == null) {
            return java.util.Collections.singletonMap("error", "Missing path parameter");
        }
        
        Path fullPath;
        if (pathStr.startsWith("/") || pathStr.matches("^[a-zA-Z]:\\\\.*")) {
            fullPath = Paths.get(pathStr);
        } else {
            fullPath = Paths.get(basePath, pathStr);
        }

        try {
            String result;
            switch (operation) {
                case "read":
                    result = readFile(fullPath);
                    break;
                case "write":
                    String content = (String) parameters.get("content");
                    if (content == null) {
                        return java.util.Collections.singletonMap("error", "Missing content parameter for write operation");
                    }
                    result = writeFile(fullPath, content);
                    break;
                case "list":
                    result = listFiles(fullPath);
                    break;
                default:
                    return java.util.Collections.singletonMap("error", "Unknown operation: " + operation);
            }
            return java.util.Collections.singletonMap("result", result);
        } catch (Exception e) {
            return java.util.Collections.singletonMap("error", "Error: " + e.getMessage());
        }
    }

    private String readFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return "错误：文件不存在：" + path.toAbsolutePath();
        }
        if (!Files.isReadable(path)) {
            return "错误：文件不可读：" + path.toAbsolutePath();
        }
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return "File written successfully: " + path;
    }

    private String listFiles(Path path) throws IOException {
        if (!Files.exists(path)) {
            return "Directory does not exist: " + path;
        }
        if (!Files.isDirectory(path)) {
            return "Not a directory: " + path;
        }
        return Files.list(path)
                .map(Path::toString)
                .collect(Collectors.joining("\n"));
    }

}
