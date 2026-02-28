package com.codingagent.cli.command;

import com.codingagent.agent.manager.AgentManager;
import picocli.CommandLine;

@CommandLine.Command(name = "generate", description = "Generate code based on requirements")
public class GenerateCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--session"}, defaultValue = "default", description = "Session ID")
    private String sessionId;

    @CommandLine.Option(names = {"-r", "--requirements"}, required = true, description = "Code requirements")
    private String requirements;

    @Override
    public void run() {
        AgentManager agentManager = new AgentManager();

        System.out.println("Generating code...");
        String result = agentManager.processTask(sessionId, "generator", requirements);
        System.out.println("\nGenerated Code:");
        System.out.println(result);
    }

}
