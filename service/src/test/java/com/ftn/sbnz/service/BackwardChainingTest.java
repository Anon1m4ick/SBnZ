package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ftn.sbnz.model.DTCCode;
import com.ftn.sbnz.model.DiagnosticRequest;
import com.ftn.sbnz.model.Symptom;
import com.ftn.sbnz.model.Vehicle;
import com.ftn.sbnz.model.VehicleThresholds;
import com.ftn.sbnz.model.enums.DTCStatus;
import com.ftn.sbnz.model.enums.EngineType;
import com.ftn.sbnz.model.enums.SymptomType;
import com.ftn.sbnz.model.enums.VehicleSystem;

@SpringBootTest(properties = {
        "debug=false",
        "logging.level.org.springframework=INFO"
})
class BackwardChainingTest {

    @Autowired
    private DiagnosticService diagnosticService;

    @Test
    void isRootCauseFindsTransitiveCauseForBlackSmoke() {
        List<String> rootCauses = diagnosticService.findRootCauses("BLACK_SMOKE");

        assertThat(rootCauses).contains("EGR_VALVE", "CLOGGED_DPF");
    }

    @Test
    void lambdaHypothesisConfirmedForLambdaDtcScenario() {
        DiagnosticRequest request = new DiagnosticRequest(
                new Vehicle("VIN-BC-001", "Toyota", "Corolla", EngineType.PETROL, 2015, 120000, true),
                new VehicleThresholds(EngineType.PETROL, 110.0, 1.0, 10.0, 2.3),
                List.of(new Symptom(SymptomType.INCREASED_FUEL_CONSUMPTION, null, 3, "Higher fuel use")),
                List.of(new DTCCode("P0131", DTCStatus.ACTIVE, VehicleSystem.FUEL_AIR_SYSTEM)),
                List.of());

        BackwardVerificationResult result = diagnosticService.verifyLambdaHypothesis(request);

        assertThat(result.isConfirmed()).isTrue();
        assertThat(result.getQueryName()).isEqualTo("lambdaHypothesis");
        assertThat(result.getMatchingBindings()).isGreaterThan(0);
    }

    @Test
    void lambdaHypothesisRejectedWithoutSupportingFacts() {
        DiagnosticRequest request = new DiagnosticRequest(
                new Vehicle("VIN-BC-002", "Toyota", "Corolla", EngineType.PETROL, 2015, 120000, true),
                new VehicleThresholds(EngineType.PETROL, 110.0, 1.0, 10.0, 2.3),
                List.of(),
                List.of(),
                List.of());

        BackwardVerificationResult result = diagnosticService.verifyLambdaHypothesis(request);

        assertThat(result.isConfirmed()).isFalse();
    }
}
