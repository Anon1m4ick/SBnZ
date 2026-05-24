package com.ftn.sbnz.model;

import java.time.LocalDateTime;

public class SensorReading {
    private String name;
    private double value;
    private LocalDateTime timestamp;

    public SensorReading() {
    }

    public SensorReading(String name, double value) {
        this(name, value, LocalDateTime.now());
    }

    public SensorReading(String name, double value, LocalDateTime timestamp) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
