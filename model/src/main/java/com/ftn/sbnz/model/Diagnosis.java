package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.enums.Component;
import com.ftn.sbnz.model.enums.Urgency;
import com.ftn.sbnz.model.enums.VehicleSystem;

public class Diagnosis {
    private Component component;
    private VehicleSystem system;
    private double confidence;
    private Urgency urgency;
    private String recommendationText;
    private List<String> ruleTrace = new ArrayList<>();

    public Diagnosis() {
    }

    public Diagnosis(Component component, VehicleSystem system, double confidence, Urgency urgency,
            String recommendationText, List<String> ruleTrace) {
        this.component = component;
        this.system = system;
        this.confidence = confidence;
        this.urgency = urgency;
        this.recommendationText = recommendationText;
        this.ruleTrace = ruleTrace;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public VehicleSystem getSystem() {
        return system;
    }

    public void setSystem(VehicleSystem system) {
        this.system = system;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    public List<String> getRuleTrace() {
        return ruleTrace;
    }

    public void setRuleTrace(List<String> ruleTrace) {
        this.ruleTrace = ruleTrace;
    }
}
