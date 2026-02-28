package com.codingagent.agent.react;

import java.util.ArrayList;
import java.util.List;

public class ReActState {
    private String sessionId;
    private String task;
    private List<Thought> thoughts;
    private List<Action> actions;
    private List<String> observations;
    private String finalAnswer;
    private int iterationCount;
    private int maxIterations;

    public ReActState(String sessionId, String task, int maxIterations) {
        this.sessionId = sessionId;
        this.task = task;
        this.thoughts = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.observations = new ArrayList<>();
        this.iterationCount = 0;
        this.maxIterations = maxIterations;
    }

    public void addThought(Thought thought) {
        thoughts.add(thought);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void addObservation(String observation) {
        observations.add(observation);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTask() {
        return task;
    }

    public List<Thought> getThoughts() {
        return thoughts;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<String> getObservations() {
        return observations;
    }

    public String getFinalAnswer() {
        return finalAnswer;
    }

    public void setFinalAnswer(String finalAnswer) {
        this.finalAnswer = finalAnswer;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void incrementIteration() {
        this.iterationCount++;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public boolean shouldContinue() {
        return iterationCount < maxIterations && finalAnswer == null;
    }

    public String buildContext() {
        StringBuilder context = new StringBuilder();
        context.append("Task: ").append(task).append("\n\n");
        
        if (!thoughts.isEmpty()) {
            context.append("Thought History:\n");
            for (Thought thought : thoughts) {
                context.append(thought.toString()).append("\n");
            }
            context.append("\n");
        }

        if (!observations.isEmpty()) {
            context.append("Observations:\n");
            for (String obs : observations) {
                context.append(obs).append("\n");
            }
            context.append("\n");
        }

        return context.toString();
    }
}
