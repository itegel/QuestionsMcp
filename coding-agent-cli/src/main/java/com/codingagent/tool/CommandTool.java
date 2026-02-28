package com.codingagent.tool;

import com.codingagent.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Collections;

public class CommandTool implements Tool {

    private final boolean enabled;

    public CommandTool() {
        this.enabled = ConfigLoader.getBooleanProperty("tool.command.enabled", true);
    }

    @Override
    public String getName() {
        return "command";
    }

    @Override
    public String getDescription() {
        return "Command execution tool. Usage: command [command]";
    }

    @Override
    public String execute(String[] args) {
        if (!enabled) {
            return "Command tool is disabled";
        }

        if (args.length < 2) {
            return "Insufficient arguments. Usage: command [command]";
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        
        return runCommand(commandBuilder.toString().trim());
    }

    @Override
    public Map<String, Object> executeWithMap(Map<String, Object> parameters) {
        if (!enabled) {
            return Collections.singletonMap("error", "Command tool is disabled");
        }

        String command = (String) parameters.get("command");
        if (command == null) {
            // Fallback to searching for any key if 'command' is not provided
            command = parameters.values().stream()
                    .filter(v -> v instanceof String)
                    .map(v -> (String) v)
                    .findFirst()
                    .orElse(null);
        }

        if (command == null) {
            return Collections.singletonMap("error", "Missing command parameter");
        }

        return Collections.singletonMap("result", runCommand(command));
    }

    private String runCommand(String command) {
        try {
            // Use ProcessBuilder with shell to handle complex commands and pipes
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("sh", "-c", command);
            }

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return "Command failed with exit code " + exitCode + "\nError: " + error.toString() + "\nOutput: " + output.toString();
            }

            return output.toString();
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }

}
