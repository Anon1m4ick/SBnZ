package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.enums.Component;
import com.ftn.sbnz.model.enums.Urgency;
import com.ftn.sbnz.model.enums.VehicleSystem;

public class FaultCandidate {
    private Component component;
    private VehicleSystem system;
    private double confidence;
    private Urgency urgency;
    private List<String> evidence = new ArrayList<>();

    public FaultCandidate() {
    }

    public FaultCandidate(Component component, VehicleSystem system, double confidence, Urgency urgency,
            List<String> evidence) {
        this.component = component;
        this.system = system;
        this.confidence = confidence;
        this.urgency = urgency;
        this.evidence = evidence;
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

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
}
