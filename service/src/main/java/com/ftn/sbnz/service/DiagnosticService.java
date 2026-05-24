package com.ftn.sbnz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ClassObjectFilter;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.DTCCode;
import com.ftn.sbnz.model.DiagnosticReport;
import com.ftn.sbnz.model.DiagnosticRequest;
import com.ftn.sbnz.model.Diagnosis;
import com.ftn.sbnz.model.FaultCandidate;
import com.ftn.sbnz.model.Recommendation;
import com.ftn.sbnz.model.RuleTrace;
import com.ftn.sbnz.model.SensorReading;
import com.ftn.sbnz.model.Symptom;
import com.ftn.sbnz.model.SystemProblem;

@Service
public class DiagnosticService {
    private final KieBase kieBase;

    public DiagnosticService(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public DiagnosticReport runDemo() {
        return evaluate(DemoDataFactory.dieselGolfScenario());
    }

    public DiagnosticReport runTransmissionDemo() {
        return evaluate(DemoDataFactory.transmissionScenario());
    }

    public DiagnosticRequest demoRequest() {
        return DemoDataFactory.dieselGolfScenario();
    }

    public DiagnosticRequest transmissionDemoRequest() {
        return DemoDataFactory.transmissionScenario();
    }

    public List<RuleInfo> ruleCatalog() {
        return List.of(
                new RuleInfo("M1-BR-03", "Member 1", "Symptom and OBD-II diagnosis",
                        "Coolant warning plus temperature above threshold creates a cooling system problem."),
                new RuleInfo("M1-BR-01", "Member 1", "Symptom and OBD-II diagnosis",
                        "Check Engine plus P0300 marks the ignition system as suspect."),
                new RuleInfo("M1-BR-02", "Member 1", "Symptom and OBD-II diagnosis",
                        "Cylinder misfire DTC plus power loss creates an ignition coil candidate."),
                new RuleInfo("M1-BR-04", "Member 1", "Symptom and OBD-II diagnosis",
                        "Oil pressure warning plus pressure below minimum creates a critical oil pump candidate."),
                new RuleInfo("M1-BR-05", "Member 1", "Symptom and OBD-II diagnosis",
                        "Diesel P0401, black smoke, and low MAF create an EGR valve candidate."),
                new RuleInfo("M1-BR-06", "Member 1", "Symptom and OBD-II diagnosis",
                        "Lambda sensor DTC plus increased fuel consumption creates a lambda sensor candidate."),
                new RuleInfo("M1-BR-07", "Member 1", "Symptom and OBD-II diagnosis",
                        "Battery warning plus low voltage while running creates a charging system candidate."),
                new RuleInfo("M1-BR-08", "Member 1", "Symptom and OBD-II diagnosis",
                        "Brake squeal plus braking vibration creates a brake pad or warped disc candidate."),
                new RuleInfo("M1-BR-09", "Member 1", "Symptom and OBD-II diagnosis",
                        "TPMS warning plus pressure 10-20 percent below recommendation creates a tyre pressure candidate."),
                new RuleInfo("M1-BR-10", "Member 1", "Symptom and OBD-II diagnosis",
                        "Gear-change jerking without engine misfire DTC creates a transmission or clutch candidate."),
                new RuleInfo("M2-BR-01", "Member 2", "Fault prioritization and repair recommendation",
                        "Critical diagnoses create an immediate stop-driving recommendation."),
                new RuleInfo("M2-BR-02", "Member 2", "Fault prioritization and repair recommendation",
                        "Two high-urgency diagnoses in the same system escalate combined risk to critical."),
                new RuleInfo("M2-BR-03", "Member 2", "Fault prioritization and repair recommendation",
                        "Electrical system diagnoses are routed to an auto-electrician."),
                new RuleInfo("M2-BR-04", "Member 2", "Fault prioritization and repair recommendation",
                        "Brake system diagnoses are routed to immediate brake service."),
                new RuleInfo("M2-BR-05", "Member 2", "Fault prioritization and repair recommendation",
                        "Low tyre pressure diagnoses are routed to a tyre shop with monitoring guidance."),
                new RuleInfo("M2-BR-06", "Member 2", "Fault prioritization and repair recommendation",
                        "Lambda sensor diagnoses get a low-to-medium cost estimate."),
                new RuleInfo("M2-BR-07", "Member 2", "Fault prioritization and repair recommendation",
                        "Turbo actuator diagnoses get a high cost estimate."),
                new RuleInfo("M2-BR-08", "Member 2", "Fault prioritization and repair recommendation",
                        "White smoke with overheating creates a head gasket or coolant leak risk warning."),
                new RuleInfo("M2-BR-09", "Member 2", "Fault prioritization and repair recommendation",
                        "EGR diagnosis plus black smoke adds a secondary DPF risk warning."),
                new RuleInfo("M2-BR-10", "Member 2", "Fault prioritization and repair recommendation",
                        "Low-confidence non-critical diagnoses recommend monitoring or a repeated scan before repair."));
    }

    public DiagnosticReport evaluate(DiagnosticRequest request) {
        KieSession kieSession = kieBase.newKieSession();
        List<String> firedRuleNames = new ArrayList<>();
        kieSession.setGlobal("ruleTrace", firedRuleNames);

        try {
            kieSession.insert(request.getVehicle());
            kieSession.insert(request.getThresholds());
            for (Symptom symptom : request.getSymptoms()) {
                kieSession.insert(symptom);
            }
            for (DTCCode dtcCode : request.getDtcCodes()) {
                kieSession.insert(dtcCode);
            }
            for (SensorReading sensorReading : request.getSensorReadings()) {
                kieSession.insert(sensorReading);
            }

            int firedRuleCount = kieSession.fireAllRules();

            List<SystemProblem> systemProblems = sortedByUrgency(collectFacts(kieSession, SystemProblem.class));
            List<FaultCandidate> faultCandidates = sortedCandidates(collectFacts(kieSession, FaultCandidate.class));
            List<Diagnosis> diagnoses = sortedDiagnoses(collectFacts(kieSession, Diagnosis.class));
            List<Recommendation> recommendations = sortedRecommendations(collectFacts(kieSession, Recommendation.class));
            RuleTrace ruleTrace = new RuleTrace(new ArrayList<>(firedRuleNames), LocalDateTime.now(),
                    "Rules fired for the demo vehicle diagnostic session.");

            return new DiagnosticReport(request.getVehicle(), systemProblems, faultCandidates, diagnoses,
                    recommendations, ruleTrace, firedRuleCount);
        } finally {
            kieSession.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> collectFacts(KieSession kieSession, Class<T> factType) {
        Collection<?> facts = kieSession.getObjects(new ClassObjectFilter(factType));
        return facts.stream().map(fact -> (T) fact).toList();
    }

    private List<SystemProblem> sortedByUrgency(List<SystemProblem> facts) {
        return facts.stream()
                .sorted(Comparator.comparingInt((SystemProblem fact) -> fact.getUrgency().getPriority()).reversed())
                .toList();
    }

    private List<FaultCandidate> sortedCandidates(List<FaultCandidate> facts) {
        return facts.stream()
                .sorted(Comparator.comparingInt((FaultCandidate fact) -> fact.getUrgency().getPriority()).reversed()
                        .thenComparing(Comparator.comparingDouble(FaultCandidate::getConfidence).reversed()))
                .toList();
    }

    private List<Diagnosis> sortedDiagnoses(List<Diagnosis> facts) {
        return facts.stream()
                .sorted(Comparator.comparingInt((Diagnosis fact) -> fact.getUrgency().getPriority()).reversed()
                        .thenComparing(Comparator.comparingDouble(Diagnosis::getConfidence).reversed()))
                .toList();
    }

    private List<Recommendation> sortedRecommendations(List<Recommendation> facts) {
        return facts.stream()
                .sorted(Comparator.comparingInt((Recommendation fact) -> fact.getUrgency().getPriority()).reversed())
                .toList();
    }
}
