package com.ftn.sbnz.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RuleTrace {
    private List<String> firedRuleNames = new ArrayList<>();
    private LocalDateTime timestamp;
    private String explanation;

    public RuleTrace() {
    }

    public RuleTrace(List<String> firedRuleNames, LocalDateTime timestamp, String explanation) {
        this.firedRuleNames = firedRuleNames;
        this.timestamp = timestamp;
        this.explanation = explanation;
    }

    public List<String> getFiredRuleNames() {
        return firedRuleNames;
    }

    public void setFiredRuleNames(List<String> firedRuleNames) {
        this.firedRuleNames = firedRuleNames;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
