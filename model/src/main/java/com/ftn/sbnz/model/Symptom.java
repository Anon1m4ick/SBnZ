package com.ftn.sbnz.model;

import com.ftn.sbnz.model.enums.OccurrenceContext;
import com.ftn.sbnz.model.enums.SymptomType;

public class Symptom {
    private SymptomType type;
    private OccurrenceContext context;
    private int severity;
    private String detail;

    public Symptom() {
    }

    public Symptom(SymptomType type, OccurrenceContext context, int severity, String detail) {
        this.type = type;
        this.context = context;
        this.severity = severity;
        this.detail = detail;
    }

    public SymptomType getType() {
        return type;
    }

    public void setType(SymptomType type) {
        this.type = type;
    }

    public OccurrenceContext getContext() {
        return context;
    }

    public void setContext(OccurrenceContext context) {
        this.context = context;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
