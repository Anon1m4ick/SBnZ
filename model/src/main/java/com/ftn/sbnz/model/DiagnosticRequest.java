package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticRequest {
    private Vehicle vehicle;
    private VehicleThresholds thresholds;
    private List<Symptom> symptoms = new ArrayList<>();
    private List<DTCCode> dtcCodes = new ArrayList<>();
    private List<SensorReading> sensorReadings = new ArrayList<>();

    public DiagnosticRequest() {
    }

    public DiagnosticRequest(Vehicle vehicle, VehicleThresholds thresholds, List<Symptom> symptoms,
            List<DTCCode> dtcCodes, List<SensorReading> sensorReadings) {
        this.vehicle = vehicle;
        this.thresholds = thresholds;
        this.symptoms = symptoms;
        this.dtcCodes = dtcCodes;
        this.sensorReadings = sensorReadings;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public VehicleThresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(VehicleThresholds thresholds) {
        this.thresholds = thresholds;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public List<DTCCode> getDtcCodes() {
        return dtcCodes;
    }

    public void setDtcCodes(List<DTCCode> dtcCodes) {
        this.dtcCodes = dtcCodes;
    }

    public List<SensorReading> getSensorReadings() {
        return sensorReadings;
    }

    public void setSensorReadings(List<SensorReading> sensorReadings) {
        this.sensorReadings = sensorReadings;
    }
}
