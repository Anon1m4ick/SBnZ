package com.ftn.sbnz.service;

import java.util.List;

import com.ftn.sbnz.model.SystemProblem;
import com.ftn.sbnz.model.enums.EngineType;

public class TemplateScenarioResult {
    private final EngineType engineType;
    private final double coolantTemp;
    private final double oilPressure;
    private final double voltage;
    private final int firedRuleCount;
    private final List<String> firedRules;
    private final List<SystemProblem> systemProblems;

    public TemplateScenarioResult(EngineType engineType, double coolantTemp, double oilPressure, double voltage,
            int firedRuleCount, List<String> firedRules, List<SystemProblem> systemProblems) {
        this.engineType = engineType;
        this.coolantTemp = coolantTemp;
        this.oilPressure = oilPressure;
        this.voltage = voltage;
        this.firedRuleCount = firedRuleCount;
        this.firedRules = firedRules;
        this.systemProblems = systemProblems;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public double getCoolantTemp() {
        return coolantTemp;
    }

    public double getOilPressure() {
        return oilPressure;
    }

    public double getVoltage() {
        return voltage;
    }

    public int getFiredRuleCount() {
        return firedRuleCount;
    }

    public List<String> getFiredRules() {
        return firedRules;
    }

    public List<SystemProblem> getSystemProblems() {
        return systemProblems;
    }
}
