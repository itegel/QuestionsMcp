package com.codingagent.cli.command;

import com.codingagent.agent.manager.AgentManager;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "analyze", description = "Analyze code structure and functionality")
public class AnalyzeCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--session"}, defaultValue = "default", description = "Session ID")
    private String sessionId;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the code file")
    private String filePath;

    @CommandLine.Option(names = {"-c", "--code"}, description = "Code to analyze")
    private String code;

    @Override
    public void run() {
        AgentManager agentManager = new AgentManager();

        String codeToAnalyze;
        if (filePath != null) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                codeToAnalyze = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return;
            }
        } else if (code != null) {
            codeToAnalyze = code;
        } else {
            System.err.println("Either --file or --code must be specified");
            return;
        }

        System.out.println("Analyzing code...");
        String result = agentManager.processTask(sessionId, "analyzer", codeToAnalyze);
        System.out.println("\nAnalysis Result:");
        System.out.println(result);
    }

}
