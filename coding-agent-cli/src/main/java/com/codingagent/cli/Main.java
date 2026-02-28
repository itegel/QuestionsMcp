package com.codingagent.cli;

import com.codingagent.cli.command.*;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new RootCommand());
        commandLine.addSubcommand("chat", new ChatCommand());
        commandLine.addSubcommand("analyze", new AnalyzeCommand());
        commandLine.addSubcommand("generate", new GenerateCommand());
        commandLine.addSubcommand("review", new ReviewCommand());
        commandLine.addSubcommand("file", new FileCommand());
        commandLine.addSubcommand("command", new CommandCommand());
        commandLine.addSubcommand("config", new ConfigCommand());
        
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

}
