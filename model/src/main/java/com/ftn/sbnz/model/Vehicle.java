package com.ftn.sbnz.model;

import com.ftn.sbnz.model.enums.EngineType;

public class Vehicle {
    private String vehicleId;
    private String make;
    private String model;
    private EngineType engineType;
    private int modelYear;
    private int odometerKm;
    private boolean engineRunning;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, String make, String model, EngineType engineType, int modelYear,
            int odometerKm, boolean engineRunning) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.engineType = engineType;
        this.modelYear = modelYear;
        this.odometerKm = odometerKm;
        this.engineRunning = engineRunning;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public int getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(int odometerKm) {
        this.odometerKm = odometerKm;
    }

    public boolean isEngineRunning() {
        return engineRunning;
    }

    public void setEngineRunning(boolean engineRunning) {
        this.engineRunning = engineRunning;
    }
}
