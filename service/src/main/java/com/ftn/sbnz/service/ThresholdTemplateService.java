package com.ftn.sbnz.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.SensorReading;
import com.ftn.sbnz.model.SystemProblem;
import com.ftn.sbnz.model.Vehicle;
import com.ftn.sbnz.model.enums.EngineType;

/**
 * Demonstrates the Drools rule-template mechanism (.drt) from the proposal
 * section 4.3 "Template Mechanism - Parameterization by Vehicle Type".
 *
 * The template {@code sensor_thresholds.drt} plus the threshold table below are
 * compiled at runtime into concrete DRL rules - one rule set per engine type -
 * instead of hand-writing a separate rule for petrol/diesel/hybrid.
 */
@Service
public class ThresholdTemplateService {
    private static final String TEMPLATE_PATH = "/templatetable/sensor_thresholds.drt";

    /** Parameter table from the proposal (coolant / oil pressure / charging voltage per engine type). */
    private final List<SensorThresholdRow> thresholdTable = List.of(
            new SensorThresholdRow(EngineType.PETROL, 110.0, 1.0, 13.5, 14.8),
            new SensorThresholdRow(EngineType.DIESEL, 105.0, 1.2, 13.5, 14.8),
            new SensorThresholdRow(EngineType.HYBRID, 100.0, 0.9, 13.8, 15.2));

    /** Compiles the .drt template against the parameter table into plain DRL text. */
    public String generateDrl() {
        try (InputStream template = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (template == null) {
                throw new IllegalStateException("Template not found on classpath: " + TEMPLATE_PATH);
            }
            return new ObjectDataCompiler().compile(thresholdTable, template);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read template " + TEMPLATE_PATH, e);
        }
    }

    /** Runs one sensor scenario against the generated rules for a single engine type. */
    public TemplateScenarioResult runScenario(EngineType engineType, double coolantTemp, double oilPressure,
            double voltage) {
        KieSession session = buildSession();
        List<String> firedRules = new ArrayList<>();
        session.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                firedRules.add(event.getMatch().getRule().getName());
            }
        });
        try {
            Vehicle vehicle = new Vehicle("VIN-TPL", "Demo", "Template scenario", engineType, 2018, 90000, true);
            session.insert(vehicle);
            session.insert(new SensorReading("coolant_temp", coolantTemp));
            session.insert(new SensorReading("oil_pressure", oilPressure));
            session.insert(new SensorReading("battery_voltage", voltage));

            int firedRuleCount = session.fireAllRules();

            List<SystemProblem> problems = collect(session, SystemProblem.class);
            return new TemplateScenarioResult(engineType, coolantTemp, oilPressure, voltage, firedRuleCount,
                    firedRules, problems);
        } finally {
            session.dispose();
        }
    }

    /** Runs the same sensor readings across all engine types to show the threshold difference. */
    public List<TemplateScenarioResult> runComparison(double coolantTemp, double oilPressure, double voltage) {
        List<TemplateScenarioResult> results = new ArrayList<>();
        for (SensorThresholdRow row : thresholdTable) {
            results.add(runScenario(row.getEngineType(), coolantTemp, oilPressure, voltage));
        }
        return results;
    }

    private KieSession buildSession() {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(generateDrl(), ResourceType.DRL);

        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Template DRL compilation failed: "
                    + results.getMessages(Message.Level.ERROR));
        }
        return kieHelper.build().newKieSession();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> collect(KieSession session, Class<T> factType) {
        Collection<?> facts = session.getObjects(new ClassObjectFilter(factType));
        List<T> typed = new ArrayList<>();
        for (Object fact : facts) {
            typed.add((T) fact);
        }
        return typed;
    }
}
