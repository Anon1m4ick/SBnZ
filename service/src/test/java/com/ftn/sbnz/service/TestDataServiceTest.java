package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "debug=false",
        "logging.level.org.springframework=INFO"
})
class TestDataServiceTest {
    @Autowired
    private TestDataService testDataService;

    @Autowired
    private DiagnosticService diagnosticService;

    @Test
    void eachDatasetLoadsAndEvaluates() {
        assertThat(testDataService.listDatasets()).contains("diesel-egr", "transmission", "tyre-pressure");

        for (String dataset : testDataService.listDatasets()) {
            var request = testDataService.loadDataset(dataset);
            var report = diagnosticService.evaluate(request);

            assertThat(request.getVehicle()).isNotNull();
            assertThat(report.getDiagnoses())
                    .as("dataset %s should produce diagnoses", dataset)
                    .isNotEmpty();
        }
    }
}
