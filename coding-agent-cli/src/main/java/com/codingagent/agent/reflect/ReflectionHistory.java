package com.codingagent.agent.reflect;

import java.util.concurrent.ConcurrentHashMap;

public class ReflectionHistory {

    private static final ReflectionHistory instance = new ReflectionHistory();
    private final ConcurrentHashMap<String, ReflectionResult> reflections;

    private ReflectionHistory() {
        reflections = new ConcurrentHashMap<>();
    }

    public static ReflectionHistory getInstance() {
        return instance;
    }

    public void addReflection(String taskId, ReflectionResult reflection) {
        reflections.put(taskId, reflection);
        System.out.println("   已保存 " + reflections.size() + " 条反思记录");
    }

    public ReflectionResult getReflection(String taskId) {
        return reflections.get(taskId);
    }

    public java.util.List<ReflectionResult> getAllReflections() {
        return new java.util.ArrayList<>(reflections.values());
    }

    public ReflectionResult getLatestReflection() {
        if (reflections.isEmpty()) {
            return null;
        }
        return reflections.values().stream()
                .max(java.util.Comparator.comparingInt(ReflectionResult::getQualityScore))
                .orElse(null);
    }

    public void clear() {
        reflections.clear();
    }

    public int size() {
        return reflections.size();
    }
}
