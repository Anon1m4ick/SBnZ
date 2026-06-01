package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.ftn.sbnz.model.SystemProblem;
import com.ftn.sbnz.model.enums.EngineType;
import com.ftn.sbnz.model.enums.VehicleSystem;

class SensorThresholdTemplateTest {
    private final ThresholdTemplateService templateService = new ThresholdTemplateService();

    @Test
    void templateGeneratesOneRulePerEngineType() {
        String drl = templateService.generateDrl();
        System.out.println("===== Generated DRL from sensor_thresholds.drt =====");
        System.out.println(drl);

        assertThat(drl).contains("EngineType.PETROL");
        assertThat(drl).contains("EngineType.DIESEL");
        assertThat(drl).contains("EngineType.HYBRID");
        assertThat(drl).contains("TPL Coolant overheating");
        assertThat(drl).contains("TPL Low oil pressure");
        assertThat(drl).contains("TPL Battery voltage out of range");
    }

    @Test
    void sameCoolantReadingTriggersDifferentResultPerEngineType() {
        // 108 C is above the diesel threshold (105) but below the petrol threshold (110).
        // Voltage 14.0 is in range for every engine type, so only the coolant rule can differ.
        TemplateScenarioResult diesel = templateService.runScenario(EngineType.DIESEL, 108.0, 5.0, 14.0);
        TemplateScenarioResult petrol = templateService.runScenario(EngineType.PETROL, 108.0, 5.0, 14.0);

        assertThat(coolingProblems(diesel)).isPositive();
        assertThat(coolingProblems(petrol)).isZero();
        assertThat(diesel.getFiredRules()).anyMatch(name -> name.contains("DIESEL"));
    }

    @Test
    void lowVoltageTriggersElectricalProblemOnlyForHybrid() {
        // 13.6 V is below the hybrid minimum (13.8) but inside the petrol/diesel range (13.5-14.8).
        TemplateScenarioResult hybrid = templateService.runScenario(EngineType.HYBRID, 95.0, 5.0, 13.6);
        TemplateScenarioResult petrol = templateService.runScenario(EngineType.PETROL, 95.0, 5.0, 13.6);

        assertThat(electricalProblems(hybrid)).isPositive();
        assertThat(electricalProblems(petrol)).isZero();
        assertThat(hybrid.getFiredRules()).anyMatch(name -> name.contains("Battery voltage"));
    }

    @Test
    void comparisonRunsAllThreeEngineTypes() {
        var results = templateService.runComparison(108.0, 0.95, 13.6);

        assertThat(results).hasSize(3);
        // Oil pressure 0.95 is below petrol(1.0) and diesel(1.2) minimums, but not hybrid(0.9).
        TemplateScenarioResult hybrid = results.stream()
                .filter(r -> r.getEngineType() == EngineType.HYBRID)
                .findFirst().orElseThrow();
        assertThat(lubricationProblems(hybrid)).isZero();
        // 13.6 V is below the hybrid minimum, so the electrical rule fires for hybrid.
        assertThat(electricalProblems(hybrid)).isPositive();
    }

    private long coolingProblems(TemplateScenarioResult result) {
        return countSystem(result, VehicleSystem.COOLING_SYSTEM);
    }

    private long lubricationProblems(TemplateScenarioResult result) {
        return countSystem(result, VehicleSystem.LUBRICATION_SYSTEM);
    }

    private long electricalProblems(TemplateScenarioResult result) {
        return countSystem(result, VehicleSystem.ELECTRICAL_SYSTEM);
    }

    private long countSystem(TemplateScenarioResult result, VehicleSystem system) {
        return result.getSystemProblems().stream()
                .map(SystemProblem::getSystem)
                .filter(system::equals)
                .count();
    }
}
