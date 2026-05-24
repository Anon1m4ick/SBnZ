package com.ftn.sbnz.model;

import com.ftn.sbnz.model.enums.ProblemStatus;
import com.ftn.sbnz.model.enums.Urgency;
import com.ftn.sbnz.model.enums.VehicleSystem;

public class SystemProblem {
    private VehicleSystem system;
    private ProblemStatus status;
    private Urgency urgency;
    private String source;

    public SystemProblem() {
    }

    public SystemProblem(VehicleSystem system, ProblemStatus status, Urgency urgency, String source) {
        this.system = system;
        this.status = status;
        this.urgency = urgency;
        this.source = source;
    }

    public VehicleSystem getSystem() {
        return system;
    }

    public void setSystem(VehicleSystem system) {
        this.system = system;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
