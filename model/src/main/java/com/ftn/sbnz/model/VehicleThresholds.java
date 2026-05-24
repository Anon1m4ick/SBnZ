package com.ftn.sbnz.model;

import com.ftn.sbnz.model.enums.EngineType;

public class VehicleThresholds {
    private EngineType engineType;
    private double maxCoolantTemp;
    private double minOilPressure;
    private double minMafNormal;
    private double recommendedTyrePressure;

    public VehicleThresholds() {
    }

    public VehicleThresholds(EngineType engineType, double maxCoolantTemp, double minOilPressure,
            double minMafNormal, double recommendedTyrePressure) {
        this.engineType = engineType;
        this.maxCoolantTemp = maxCoolantTemp;
        this.minOilPressure = minOilPressure;
        this.minMafNormal = minMafNormal;
        this.recommendedTyrePressure = recommendedTyrePressure;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public double getMaxCoolantTemp() {
        return maxCoolantTemp;
    }

    public void setMaxCoolantTemp(double maxCoolantTemp) {
        this.maxCoolantTemp = maxCoolantTemp;
    }

    public double getMinOilPressure() {
        return minOilPressure;
    }

    public void setMinOilPressure(double minOilPressure) {
        this.minOilPressure = minOilPressure;
    }

    public double getMinMafNormal() {
        return minMafNormal;
    }

    public void setMinMafNormal(double minMafNormal) {
        this.minMafNormal = minMafNormal;
    }

    public double getRecommendedTyrePressure() {
        return recommendedTyrePressure;
    }

    public void setRecommendedTyrePressure(double recommendedTyrePressure) {
        this.recommendedTyrePressure = recommendedTyrePressure;
    }
}
