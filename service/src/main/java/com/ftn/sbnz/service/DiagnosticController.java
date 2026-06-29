package com.ftn.sbnz.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.DiagnosticReport;
import com.ftn.sbnz.model.DiagnosticRequest;
import com.ftn.sbnz.model.Diagnosis;
import com.ftn.sbnz.model.Recommendation;
import com.ftn.sbnz.model.RuleTrace;
import com.ftn.sbnz.model.events.CepAlert;
import com.ftn.sbnz.model.enums.EngineType;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {
    private final DiagnosticService diagnosticService;
    private final ThresholdTemplateService thresholdTemplateService;
    private final CepService cepService;
    private final TestDataService testDataService;

    public DiagnosticController(DiagnosticService diagnosticService,
            ThresholdTemplateService thresholdTemplateService,
            CepService cepService,
            TestDataService testDataService) {
        this.diagnosticService = diagnosticService;
        this.thresholdTemplateService = thresholdTemplateService;
        this.cepService = cepService;
        this.testDataService = testDataService;
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

    @GetMapping("/datasets")
    public List<String> datasets() {
        return testDataService.listDatasets();
    }

    @GetMapping("/datasets/{name}")
    public DiagnosticRequest dataset(@PathVariable String name) {
        return testDataService.loadDataset(name);
    }

    @GetMapping("/datasets/{name}/report")
    public DiagnosticReport datasetReport(@PathVariable String name) {
        return diagnosticService.evaluate(testDataService.loadDataset(name));
    }

    @GetMapping(value = "/template/drl", produces = MediaType.TEXT_PLAIN_VALUE)
    public String templateDrl() {
        return thresholdTemplateService.generateDrl();
    }

    @GetMapping("/template/demo")
    public List<TemplateScenarioResult> templateDemo(
            @RequestParam(defaultValue = "108") double coolant,
            @RequestParam(defaultValue = "0.95") double oil,
            @RequestParam(defaultValue = "13.6") double voltage) {
        return thresholdTemplateService.runComparison(coolant, oil, voltage);
    }

    @GetMapping("/template/scenario")
    public TemplateScenarioResult templateScenario(
            @RequestParam EngineType engine,
            @RequestParam(defaultValue = "108") double coolant,
            @RequestParam(defaultValue = "0.95") double oil,
            @RequestParam(defaultValue = "13.6") double voltage) {
        return thresholdTemplateService.runScenario(engine, coolant, oil, voltage);
    }

    @PostMapping
    public DiagnosticReport evaluate(@RequestBody DiagnosticRequest request) {
        return diagnosticService.evaluate(request);
    }

    @PostMapping("/evaluate")
    public DiagnosticReport evaluateExplicit(@RequestBody DiagnosticRequest request) {
        return diagnosticService.evaluate(request);
    }

    @GetMapping("/cep/overheating")
    public List<CepAlert> cepOverheatingDemo() {
        return cepService.runOverheatingDemo();
    }

    @GetMapping("/cep/voltage")
    public List<CepAlert> cepVoltageDemo() {
        return cepService.runVoltageOscillationDemo();
    }

    @GetMapping("/cep/misfire")
    public List<CepAlert> cepMisfireDemo() {
        return cepService.runSporadicMisfireDemo();
    }

    @GetMapping("/cep/maf")
    public List<CepAlert> cepMafDemo() {
        return cepService.runMafDecliningDemo();
    }

    @GetMapping("/cep/tyre-pressure")
    public List<CepAlert> cepTyrePressureDemo() {
        return cepService.runTyrePressureDropDemo();
    }

    @GetMapping("/cep/critical-warnings")
    public List<CepAlert> cepCriticalWarningsDemo() {
        return cepService.runRepeatedCriticalWarningsDemo();
    }

    @PostMapping("/backward/verify-lambda")
    public BackwardVerificationResult verifyLambdaHypothesis(@RequestBody DiagnosticRequest request) {
        return diagnosticService.verifyLambdaHypothesis(request);
    }

    @GetMapping("/backward/root-cause")
    public List<String> findRootCauses(@RequestParam String effect) {
        return diagnosticService.findRootCauses(effect);
    }
}
