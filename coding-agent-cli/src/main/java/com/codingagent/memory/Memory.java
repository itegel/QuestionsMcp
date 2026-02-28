package com.codingagent.memory;

import com.codingagent.util.QwenRequest;

import java.util.List;

public interface Memory {

    void addMessage(String sessionId, QwenRequest.Message message);
    List<QwenRequest.Message> getMessages(String sessionId);
    void clear(String sessionId);
    int getSize(String sessionId);

}
