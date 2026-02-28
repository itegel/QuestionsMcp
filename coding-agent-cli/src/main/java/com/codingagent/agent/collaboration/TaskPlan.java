package com.codingagent.agent.collaboration;

import java.util.ArrayList;
import java.util.List;

public class TaskPlan {

    private String strategy;
    private final List<Subtask> subtasks;

    public TaskPlan() {
        this.subtasks = new ArrayList<>();
        this.strategy = "general";
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public int size() {
        return subtasks.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaskPlan{strategy='").append(strategy).append("', subtasks=[\n");
        for (Subtask subtask : subtasks) {
            sb.append("  ").append(subtask).append("\n");
        }
        sb.append("]}");
        return sb.toString();
    }
}
