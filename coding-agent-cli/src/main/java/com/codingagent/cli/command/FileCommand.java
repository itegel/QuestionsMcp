package com.codingagent.cli.command;

import com.codingagent.tool.ToolManager;
import picocli.CommandLine;

@CommandLine.Command(name = "file", description = "File operations (read, write, list)")
public class FileCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Operation: read, write, list")
    private String operation;

    @CommandLine.Parameters(index = "1", description = "File path")
    private String path;

    @CommandLine.Parameters(index = "2", arity = "0..1", description = "Content (for write operation)")
    private String content;

    @Override
    public void run() {
        ToolManager toolManager = new ToolManager();

        String[] args;
        if (operation.equals("write") && content != null) {
            args = new String[]{operation, path, content};
        } else {
            args = new String[]{operation, path};
        }

        String result = toolManager.executeTool("file", args);
        System.out.println(result);
    }

}
