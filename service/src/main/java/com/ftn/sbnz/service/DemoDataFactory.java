package com.ftn.sbnz.service;

import java.util.List;

import com.ftn.sbnz.model.DTCCode;
import com.ftn.sbnz.model.DiagnosticRequest;
import com.ftn.sbnz.model.SensorReading;
import com.ftn.sbnz.model.Symptom;
import com.ftn.sbnz.model.Vehicle;
import com.ftn.sbnz.model.VehicleThresholds;
import com.ftn.sbnz.model.enums.DTCStatus;
import com.ftn.sbnz.model.enums.EngineType;
import com.ftn.sbnz.model.enums.OccurrenceContext;
import com.ftn.sbnz.model.enums.SymptomType;
import com.ftn.sbnz.model.enums.VehicleSystem;

public final class DemoDataFactory {
    private DemoDataFactory() {
    }

    public static DiagnosticRequest dieselGolfScenario() {
        Vehicle vehicle = new Vehicle("VIN-DEMO-001", "Volkswagen", "Golf 5 2.0 TDI", EngineType.DIESEL,
                2007, 187000, true);
        VehicleThresholds thresholds = new VehicleThresholds(EngineType.DIESEL, 105.0, 1.2, 12.0, 2.3);

        List<Symptom> symptoms = List.of(
                new Symptom(SymptomType.WARNING_CHECK_ENGINE, OccurrenceContext.ALWAYS, 4,
                        "Check Engine warning is active"),
                new Symptom(SymptomType.WARNING_COOLANT_TEMP, OccurrenceContext.EXTENDED_DRIVING, 4,
                        "Coolant temperature warning is active"),
                new Symptom(SymptomType.WARNING_OIL_PRESSURE, OccurrenceContext.ENGINE_RUNNING, 5,
                        "Oil pressure warning is active"),
                new Symptom(SymptomType.WARNING_BATTERY, OccurrenceContext.ENGINE_RUNNING, 4,
                        "Battery warning is active"),
                new Symptom(SymptomType.BLACK_EXHAUST_SMOKE, OccurrenceContext.ACCELERATION, 4,
                        "Black smoke under acceleration"),
                new Symptom(SymptomType.WHITE_EXHAUST_SMOKE, OccurrenceContext.EXTENDED_DRIVING, 5,
                        "White smoke is visible during overheating"),
                new Symptom(SymptomType.POWER_LOSS, OccurrenceContext.ACCELERATION, 3,
                        "Noticeable power loss"),
                new Symptom(SymptomType.INCREASED_FUEL_CONSUMPTION, OccurrenceContext.ALWAYS, 3,
                        "Fuel consumption increased"),
                new Symptom(SymptomType.BRAKE_SQUEAL, OccurrenceContext.BRAKING, 4,
                        "Brake squeal while braking"),
                new Symptom(SymptomType.STEERING_WHEEL_VIBRATION, OccurrenceContext.BRAKING, 4,
                        "Steering wheel vibration while braking"),
                new Symptom(SymptomType.TPMS_WARNING, OccurrenceContext.ALWAYS, 2,
                        "TPMS warning is active"));

        List<DTCCode> dtcCodes = List.of(
                new DTCCode("P0401", DTCStatus.ACTIVE, VehicleSystem.EGR_SYSTEM),
                new DTCCode("P0300", DTCStatus.ACTIVE, VehicleSystem.IGNITION_SYSTEM),
                new DTCCode("P0302", DTCStatus.ACTIVE, VehicleSystem.IGNITION_SYSTEM),
                new DTCCode("P0131", DTCStatus.ACTIVE, VehicleSystem.FUEL_AIR_SYSTEM),
                new DTCCode("P0234", DTCStatus.ACTIVE, VehicleSystem.TURBO_SYSTEM));

        List<SensorReading> sensorReadings = List.of(
                new SensorReading("coolant_temp", 108.0),
                new SensorReading("oil_pressure", 0.6),
                new SensorReading("maf", 8.2),
                new SensorReading("battery_voltage", 11.6),
                new SensorReading("boost_pressure", 2.3),
                new SensorReading("tyre_pressure", 2.0));

        return new DiagnosticRequest(vehicle, thresholds, symptoms, dtcCodes, sensorReadings);
    }

    public static DiagnosticRequest transmissionScenario() {
        Vehicle vehicle = new Vehicle("VIN-DEMO-002", "Toyota", "Corolla 1.6", EngineType.PETROL,
                2012, 142000, true);
        VehicleThresholds thresholds = new VehicleThresholds(EngineType.PETROL, 110.0, 1.0, 10.0, 2.3);

        List<Symptom> symptoms = List.of(
                new Symptom(SymptomType.GEAR_CHANGE_JERK, OccurrenceContext.GEAR_CHANGE, 3,
                        "Jerking occurs during gear change"));

        return new DiagnosticRequest(vehicle, thresholds, symptoms, List.of(), List.of());
    }
}
