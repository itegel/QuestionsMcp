package com.codingagent.cli.command;

import com.codingagent.tool.ToolManager;
import picocli.CommandLine;

@CommandLine.Command(name = "command", description = "Execute system commands")
public class CommandCommand implements Runnable {

    @CommandLine.Parameters(index = "0..*", description = "Command to execute")
    private String[] command;

    @Override
    public void run() {
        ToolManager toolManager = new ToolManager();

        if (command == null || command.length == 0) {
            System.err.println("No command specified");
            return;
        }

        String[] args = new String[command.length + 1];
        args[0] = "command";
        System.arraycopy(command, 0, args, 1, command.length);

        String result = toolManager.executeTool("command", args);
        System.out.println(result);
    }

}
