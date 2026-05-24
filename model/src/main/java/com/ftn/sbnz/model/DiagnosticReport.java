package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticReport {
    private Vehicle vehicle;
    private List<SystemProblem> systemProblems = new ArrayList<>();
    private List<FaultCandidate> faultCandidates = new ArrayList<>();
    private List<Diagnosis> diagnoses = new ArrayList<>();
    private List<Recommendation> recommendations = new ArrayList<>();
    private RuleTrace ruleTrace;
    private int firedRuleCount;

    public DiagnosticReport() {
    }

    public DiagnosticReport(Vehicle vehicle, List<SystemProblem> systemProblems, List<FaultCandidate> faultCandidates,
            List<Diagnosis> diagnoses, List<Recommendation> recommendations, RuleTrace ruleTrace,
            int firedRuleCount) {
        this.vehicle = vehicle;
        this.systemProblems = systemProblems;
        this.faultCandidates = faultCandidates;
        this.diagnoses = diagnoses;
        this.recommendations = recommendations;
        this.ruleTrace = ruleTrace;
        this.firedRuleCount = firedRuleCount;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<SystemProblem> getSystemProblems() {
        return systemProblems;
    }

    public void setSystemProblems(List<SystemProblem> systemProblems) {
        this.systemProblems = systemProblems;
    }

    public List<FaultCandidate> getFaultCandidates() {
        return faultCandidates;
    }

    public void setFaultCandidates(List<FaultCandidate> faultCandidates) {
        this.faultCandidates = faultCandidates;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public RuleTrace getRuleTrace() {
        return ruleTrace;
    }

    public void setRuleTrace(RuleTrace ruleTrace) {
        this.ruleTrace = ruleTrace;
    }

    public int getFiredRuleCount() {
        return firedRuleCount;
    }

    public void setFiredRuleCount(int firedRuleCount) {
        this.firedRuleCount = firedRuleCount;
    }

    public String toConsoleString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle Fault Diagnostics demo result\n");
        builder.append("Fired rules: ").append(firedRuleCount).append("\n");
        builder.append("Trace: ").append(ruleTrace.getFiredRuleNames()).append("\n");
        for (Diagnosis diagnosis : diagnoses) {
            builder.append("- ")
                    .append(diagnosis.getComponent())
                    .append(" | ")
                    .append(diagnosis.getUrgency())
                    .append(" | ")
                    .append(Math.round(diagnosis.getConfidence() * 100))
                    .append("% | ")
                    .append(diagnosis.getRecommendationText())
                    .append("\n");
        }
        for (Recommendation recommendation : recommendations) {
            builder.append("  Recommendation: ")
                    .append(recommendation.getAction())
                    .append(" via ")
                    .append(recommendation.getServiceType())
                    .append(" - ")
                    .append(recommendation.getWarningText())
                    .append("\n");
        }
        return builder.toString();
    }
}
