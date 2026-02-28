package com.codingagent.tool;

import com.codingagent.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        try {
            StringBuilder command = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                command.append(args[i]).append(" ");
            }

            Process process = Runtime.getRuntime().exec(command.toString().trim());
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
                return "Command failed with exit code " + exitCode + "\nError: " + error.toString();
            }

            return output.toString();
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }

}
