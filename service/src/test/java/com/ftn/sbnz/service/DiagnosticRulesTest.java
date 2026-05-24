package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ftn.sbnz.model.DiagnosticReport;
import com.ftn.sbnz.model.enums.Component;
import com.ftn.sbnz.model.enums.Urgency;

@SpringBootTest(properties = {
        "debug=false",
        "logging.level.org.springframework=INFO"
})
class DiagnosticRulesTest {
    @Autowired
    private DiagnosticService diagnosticService;

    @Test
    void demoScenarioActivatesRulesAndProducesRankedDiagnoses() {
        DiagnosticReport report = diagnosticService.runDemo();

        assertThat(diagnosticService.ruleCatalog()).hasSize(20);
        assertThat(report.getFiredRuleCount()).isGreaterThanOrEqualTo(20);
        assertThat(report.getDiagnoses()).extracting("component")
                .contains(Component.OIL_PUMP, Component.EGR_VALVE, Component.IGNITION_COIL,
                        Component.LOW_TYRE_PRESSURE, Component.LAMBDA_SENSOR, Component.ALTERNATOR,
                        Component.BRAKE_PAD_OR_DISC, Component.TURBO_ACTUATOR);
        assertThat(report.getDiagnoses().get(0).getUrgency()).isEqualTo(Urgency.CRITICAL);
        assertThat(report.getRecommendations().get(0).getAction().name()).isEqualTo("STOP_DRIVING_IMMEDIATELY");
    }

    @Test
    void transmissionScenarioActivatesNoMisfireForwardRule() {
        DiagnosticReport report = diagnosticService.runTransmissionDemo();

        assertThat(report.getRuleTrace().getFiredRuleNames())
                .anyMatch(name -> name.startsWith("M1-BR-10"));
        assertThat(report.getDiagnoses()).extracting("component")
                .contains(Component.TRANSMISSION_OR_CLUTCH);
    }
}
