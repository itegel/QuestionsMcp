package com.codingagent.agent.reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionResult {

    private String taskId;
    private int qualityScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> improvements;
    private String lessonsLearned;
    private boolean shouldRetry;
    private String retryStrategy;

    public ReflectionResult() {
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.improvements = new ArrayList<>();
        this.qualityScore = 5;
        this.shouldRetry = false;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public void addStrength(String strength) {
        this.strengths.add(strength);
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public void addWeakness(String weakness) {
        this.weaknesses.add(weakness);
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }

    public void addImprovement(String improvement) {
        this.improvements.add(improvement);
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }

    public boolean isShouldRetry() {
        return shouldRetry;
    }

    public void setShouldRetry(boolean shouldRetry) {
        this.shouldRetry = shouldRetry;
    }

    public String getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(String retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 反思结果 ===\n");
        sb.append("任务 ID: ").append(taskId).append("\n");
        sb.append("质量评分：").append(qualityScore).append("/10\n");
        
        if (!strengths.isEmpty()) {
            sb.append("\n✅ 优势:\n");
            for (String s : strengths) {
                sb.append("  - ").append(s).append("\n");
            }
        }
        
        if (!weaknesses.isEmpty()) {
            sb.append("\n⚠️  不足:\n");
            for (String w : weaknesses) {
                sb.append("  - ").append(w).append("\n");
            }
        }
        
        if (!improvements.isEmpty()) {
            sb.append("\n💡 改进建议:\n");
            for (String i : improvements) {
                sb.append("  - ").append(i).append("\n");
            }
        }
        
        if (lessonsLearned != null && !lessonsLearned.isEmpty()) {
            sb.append("\n📚 经验教训:\n");
            sb.append("  ").append(lessonsLearned).append("\n");
        }
        
        if (shouldRetry) {
            sb.append("\n🔄 建议重试:\n");
            sb.append("  策略：").append(retryStrategy).append("\n");
        }
        
        return sb.toString();
    }
}
