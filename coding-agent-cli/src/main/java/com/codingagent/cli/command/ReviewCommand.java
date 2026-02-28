package com.codingagent.cli.command;

import com.codingagent.agent.manager.AgentManager;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "review", description = "Review code quality and security")
public class ReviewCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--session"}, defaultValue = "default", description = "Session ID")
    private String sessionId;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the code file")
    private String filePath;

    @CommandLine.Option(names = {"-c", "--code"}, description = "Code to review")
    private String code;

    @Override
    public void run() {
        AgentManager agentManager = new AgentManager();

        String codeToReview;
        if (filePath != null) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                codeToReview = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return;
            }
        } else if (code != null) {
            codeToReview = code;
        } else {
            System.err.println("Either --file or --code must be specified");
            return;
        }

        System.out.println("Reviewing code...");
        String result = agentManager.processTask(sessionId, "reviewer", codeToReview);
        System.out.println("\nReview Result:");
        System.out.println(result);
    }

}
