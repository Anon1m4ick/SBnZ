package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ftn.sbnz.model.events.CepAlert;

@SpringBootTest(properties = {
        "debug=false",
        "logging.level.org.springframework=INFO"
})
class CepRulesTest {

    @Autowired
    private CepService cepService;

    @Test
    void overheatingDemoUsesPseudoClockAndCreatesAlert() {
        var alerts = cepService.runOverheatingDemo();

        assertThat(alerts).isNotEmpty();
        assertThat(alerts).extracting(CepAlert::getType).contains("OVERHEATING_TREND");
    }

    @Test
    void voltageOscillationDemoCreatesAlert() {
        var alerts = cepService.runVoltageOscillationDemo();

        assertThat(alerts).isNotEmpty();
        assertThat(alerts).extracting(CepAlert::getType).contains("VOLTAGE_OSCILLATION");
    }

    @Test
    void sporadicMisfireDemoCreatesAlert() {
        var alerts = cepService.runSporadicMisfireDemo();

        assertThat(alerts).isNotEmpty();
        assertThat(alerts).extracting(CepAlert::getType).contains("SPORADIC_MISFIRE");
    }
}
