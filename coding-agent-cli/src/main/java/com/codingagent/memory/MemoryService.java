package com.codingagent.memory;

import com.codingagent.util.QwenRequest;

import java.util.List;

public class MemoryService {

    private final Memory memory;

    public MemoryService() {
        this.memory = new InMemory();
    }

    public void addMessage(String sessionId, String role, String content) {
        QwenRequest.Message message = new QwenRequest.Message(role, content);
        memory.addMessage(sessionId, message);
    }

    public List<QwenRequest.Message> getMessages(String sessionId) {
        return memory.getMessages(sessionId);
    }

    public void clear(String sessionId) {
        memory.clear(sessionId);
    }

    public int getSize(String sessionId) {
        return memory.getSize(sessionId);
    }

}
