package com.codingagent.memory;

import com.codingagent.util.ConfigLoader;
import com.codingagent.util.QwenRequest;

import java.util.*;

public class InMemory implements Memory {

    private final Map<String, List<QwenRequest.Message>> memory = new HashMap<>();

    private final int maxContextSize;

    public InMemory() {
        this.maxContextSize = ConfigLoader.getIntProperty("memory.max-context-size", 10);
    }

    @Override
    public void addMessage(String sessionId, QwenRequest.Message message) {
        List<QwenRequest.Message> messages = memory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        messages.add(message);

        // 保持上下文大小不超过限制
        if (messages.size() > maxContextSize) {
            messages = messages.subList(messages.size() - maxContextSize, messages.size());
            memory.put(sessionId, messages);
        }
    }

    @Override
    public List<QwenRequest.Message> getMessages(String sessionId) {
        return memory.getOrDefault(sessionId, Collections.emptyList());
    }

    @Override
    public void clear(String sessionId) {
        memory.remove(sessionId);
    }

    @Override
    public int getSize(String sessionId) {
        List<QwenRequest.Message> messages = memory.get(sessionId);
        return messages != null ? messages.size() : 0;
    }

}
