package com.ftn.sbnz.service;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.DiagnosticReport;
import com.ftn.sbnz.model.DiagnosticRequest;
import com.ftn.sbnz.model.Diagnosis;
import com.ftn.sbnz.model.Recommendation;
import com.ftn.sbnz.model.RuleTrace;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {
    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @GetMapping("/demo")
    public DiagnosticReport demo() {
        return diagnosticService.runDemo();
    }

    @GetMapping("/demo/request")
    public DiagnosticRequest demoRequest() {
        return diagnosticService.demoRequest();
    }

    @GetMapping("/demo/transmission/request")
    public DiagnosticRequest transmissionDemoRequest() {
        return diagnosticService.transmissionDemoRequest();
    }

    @GetMapping("/demo/transmission/report")
    public DiagnosticReport transmissionDemoReport() {
        return diagnosticService.runTransmissionDemo();
    }

    @GetMapping("/demo/report")
    public DiagnosticReport demoReport() {
        return diagnosticService.runDemo();
    }

    @GetMapping("/demo/diagnoses")
    public List<Diagnosis> demoDiagnoses() {
        return diagnosticService.runDemo().getDiagnoses();
    }

    @GetMapping("/demo/recommendations")
    public List<Recommendation> demoRecommendations() {
        return diagnosticService.runDemo().getRecommendations();
    }

    @GetMapping("/demo/trace")
    public RuleTrace demoTrace() {
        return diagnosticService.runDemo().getRuleTrace();
    }

    @GetMapping("/rules")
    public List<RuleInfo> rules() {
        return diagnosticService.ruleCatalog();
    }

    @PostMapping
    public DiagnosticReport evaluate(@RequestBody DiagnosticRequest request) {
        return diagnosticService.evaluate(request);
    }

    @PostMapping("/evaluate")
    public DiagnosticReport evaluateExplicit(@RequestBody DiagnosticRequest request) {
        return diagnosticService.evaluate(request);
    }
}
