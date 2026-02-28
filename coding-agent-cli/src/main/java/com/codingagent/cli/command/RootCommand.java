package com.codingagent.cli.command;

import picocli.CommandLine;

@CommandLine.Command(name = "coding-agent", description = "Coding Agent CLI - A command-line tool for code analysis, generation, and review")
public class RootCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Coding Agent CLI");
        System.out.println("Usage: coding-agent <command> [options]");
        System.out.println("\nAvailable commands:");
        System.out.println("  chat       - Start a chat with the agent");
        System.out.println("  analyze    - Analyze code structure and functionality");
        System.out.println("  generate   - Generate code based on requirements");
        System.out.println("  review     - Review code quality and security");
        System.out.println("  file       - File operations (read, write, list)");
        System.out.println("  command    - Execute system commands");
        System.out.println("  config     - Manage configuration");
        System.out.println("\nUse 'coding-agent <command> --help' for more information about a command.");
    }

}
