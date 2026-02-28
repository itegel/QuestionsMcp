package com.codingagent.cli.command;

import com.codingagent.util.ConfigLoader;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@CommandLine.Command(name = "config", description = "Manage configuration")
public class ConfigCommand implements Runnable {

    @CommandLine.Option(names = {"-l", "--list"}, description = "List current configuration")
    private boolean list;

    @CommandLine.Option(names = {"-s", "--set"}, description = "Set configuration key=value")
    private String set;

    @Override
    public void run() {
        if (list) {
            listConfig();
        } else if (set != null) {
            setConfig(set);
        } else {
            System.err.println("Either --list or --set must be specified");
        }
    }

    private void listConfig() {
        System.out.println("Current configuration:");
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Could not find config.properties");
                return;
            }
            Properties properties = new Properties();
            properties.load(input);
            properties.forEach((key, value) -> System.out.println(key + " = " + value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setConfig(String keyValue) {
        String[] parts = keyValue.split("=", 2);
        if (parts.length != 2) {
            System.err.println("Invalid format. Use key=value");
            return;
        }
        String key = parts[0].trim();
        String value = parts[1].trim();

        try {
            Path configPath = Paths.get("src/main/resources/config.properties");
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
            }
            properties.setProperty(key, value);
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, null);
            }
            System.out.println("Configuration updated: " + key + " = " + value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
