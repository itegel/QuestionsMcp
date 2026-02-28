package com.codingagent.agent.collaboration;

public class Subtask {

    private final String description;
    private final String agentName;
    private final String task;

    public Subtask(String description, String agentName, String task) {
        this.description = description;
        this.agentName = agentName;
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "description='" + description + '\'' +
                ", agentName='" + agentName + '\'' +
                ", task='" + task.substring(0, Math.min(50, task.length())) + "...'}" +
                '}';
    }
}
