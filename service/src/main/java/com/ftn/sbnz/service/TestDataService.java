package com.ftn.sbnz.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.sbnz.model.DiagnosticRequest;

@Service
public class TestDataService {
    private static final String TESTDATA_ROOT = "/testdata/";
    private static final List<String> DATASET_NAMES = List.of("diesel-egr", "transmission", "tyre-pressure");

    private final ObjectMapper objectMapper;

    public TestDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> listDatasets() {
        return DATASET_NAMES;
    }

    public DiagnosticRequest loadDataset(String name) {
        if (!DATASET_NAMES.contains(name)) {
            throw new IllegalArgumentException("Unknown dataset: " + name);
        }

        String path = TESTDATA_ROOT + name + ".json";
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Dataset not found on classpath: " + path);
            }
            return objectMapper.readValue(stream, DiagnosticRequest.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read dataset: " + name, e);
        }
    }
}
