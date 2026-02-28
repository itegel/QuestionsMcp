package com.codingagent.tool;

import java.util.Map;

public interface Tool {

    String getName();
    String getDescription();
    String execute(String[] args);
    
    default Map<String, Object> executeWithMap(Map<String, Object> parameters) {
        String[] args = parameters.entrySet().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.getKey(), String.valueOf(e.getValue())))
                .toArray(String[]::new);
        return java.util.Collections.singletonMap("result", execute(args));
    }
    
    default String getSchema() {
        return "{}";
    }

}
