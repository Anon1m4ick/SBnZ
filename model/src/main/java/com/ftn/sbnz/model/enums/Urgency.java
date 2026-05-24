package com.ftn.sbnz.model.enums;

public enum Urgency {
    CRITICAL(4),
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int priority;

    Urgency(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
