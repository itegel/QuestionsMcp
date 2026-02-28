package com.codingagent.agent.react;

public class Thought {
    private String content;
    private ThoughtType type;

    public enum ThoughtType {
        REASONING,
        ACTION,
        OBSERVATION,
        FINAL_ANSWER
    }

    public Thought(String content, ThoughtType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ThoughtType getType() {
        return type;
    }

    public void setType(ThoughtType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + ": " + content;
    }
}
