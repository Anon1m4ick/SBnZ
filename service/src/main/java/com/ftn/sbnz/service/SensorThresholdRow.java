package com.ftn.sbnz.service;

import com.ftn.sbnz.model.enums.EngineType;

/**
 * One row of the sensor-threshold parameter table fed into the
 * {@code sensor_thresholds.drt} rule template. The getter names must match the
 * column names declared in the template header, mirroring the
 * {@code ClassificationTemplateModel} pattern from the lab examples.
 */
public class SensorThresholdRow {
    private final EngineType engineType;
    private final double maxCoolantTemp;
    private final double minOilPressure;
    private final double voltageMin;
    private final double voltageMax;

    public SensorThresholdRow(EngineType engineType, double maxCoolantTemp, double minOilPressure,
            double voltageMin, double voltageMax) {
        this.engineType = engineType;
        this.maxCoolantTemp = maxCoolantTemp;
        this.minOilPressure = minOilPressure;
        this.voltageMin = voltageMin;
        this.voltageMax = voltageMax;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public double getMaxCoolantTemp() {
        return maxCoolantTemp;
    }

    public double getMinOilPressure() {
        return minOilPressure;
    }

    public double getVoltageMin() {
        return voltageMin;
    }

    public double getVoltageMax() {
        return voltageMax;
    }
}
