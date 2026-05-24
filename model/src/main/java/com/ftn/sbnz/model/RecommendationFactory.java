package com.ftn.sbnz.model;

import java.util.List;

import com.ftn.sbnz.model.enums.ActionType;
import com.ftn.sbnz.model.enums.Component;
import com.ftn.sbnz.model.enums.CostRange;
import com.ftn.sbnz.model.enums.ServiceType;
import com.ftn.sbnz.model.enums.Urgency;
import com.ftn.sbnz.model.enums.VehicleSystem;

public final class RecommendationFactory {
    private RecommendationFactory() {
    }

    public static String diagnosisText(Component component) {
        return switch (component) {
            case RANDOM_MISFIRE_GROUP -> "Random misfire pattern suspected. Inspect ignition and fuel delivery.";
            case OIL_PUMP -> "Oil pump failure or severe oil loss suspected. Stop the engine immediately.";
            case EGR_VALVE -> "EGR flow restriction suspected. Inspect and clean or replace the EGR valve.";
            case IGNITION_COIL -> "Cylinder-specific misfire suspected. Check ignition coil and spark plug.";
            case THERMOSTAT -> "Thermostat stuck closed suspected. Avoid highway driving until repaired.";
            case LOW_TYRE_PRESSURE -> "Slow tyre pressure loss suspected. Inflate and monitor pressure.";
            case ALTERNATOR -> "Charging system problem suspected. Inspect alternator and battery.";
            case SPARK_PLUG -> "Worn spark plug suspected. Replace before the next service interval.";
            case LAMBDA_SENSOR -> "Lambda sensor fault suspected. Check oxygen sensor signal and wiring.";
            case BRAKE_PAD_OR_DISC -> "Brake pad wear or warped disc suspected. Inspect the braking system.";
            case TRANSMISSION_OR_CLUTCH -> "Transmission or clutch issue suspected. Inspect drivetrain before further use.";
            case TURBO_ACTUATOR -> "Turbo actuator or boost control issue suspected. Inspect boost control hardware.";
        };
    }

    public static Recommendation fromDiagnosis(Diagnosis diagnosis) {
        ActionType action = actionFor(diagnosis.getUrgency());
        ServiceType serviceType = serviceFor(diagnosis.getSystem(), diagnosis.getComponent());
        CostRange costRange = costFor(diagnosis.getComponent());
        String warning = warningFor(diagnosis);
        return new Recommendation(action, serviceType, costRange, diagnosis.getUrgency(), warning,
                List.of(diagnosis.getComponent()));
    }

    private static ActionType actionFor(Urgency urgency) {
        return switch (urgency) {
            case CRITICAL -> ActionType.STOP_DRIVING_IMMEDIATELY;
            case HIGH -> ActionType.REPAIR_WITHIN_48H;
            case MEDIUM -> ActionType.REPAIR_BEFORE_NEXT_SERVICE;
            case LOW -> ActionType.MONITOR;
        };
    }

    private static ServiceType serviceFor(VehicleSystem system, Component component) {
        if (component == Component.EGR_VALVE) {
            return ServiceType.DIESEL_SPECIALIST;
        }
        return switch (system) {
            case ELECTRICAL_SYSTEM -> ServiceType.AUTO_ELECTRICIAN;
            case BRAKE_SYSTEM -> ServiceType.BRAKE_SERVICE;
            case TYRE_SYSTEM -> ServiceType.TYRE_SHOP;
            default -> ServiceType.MECHANIC;
        };
    }

    private static CostRange costFor(Component component) {
        return switch (component) {
            case OIL_PUMP -> CostRange.HIGH;
            case TURBO_ACTUATOR -> CostRange.HIGH;
            case EGR_VALVE -> CostRange.MEDIUM;
            case LAMBDA_SENSOR, IGNITION_COIL, SPARK_PLUG -> CostRange.LOW_TO_MEDIUM;
            case LOW_TYRE_PRESSURE -> CostRange.LOW;
            case THERMOSTAT, ALTERNATOR, BRAKE_PAD_OR_DISC, TRANSMISSION_OR_CLUTCH, RANDOM_MISFIRE_GROUP -> CostRange.MEDIUM;
        };
    }

    private static String warningFor(Diagnosis diagnosis) {
        if (diagnosis.getUrgency() == Urgency.CRITICAL) {
            return "Do not continue driving. Engine damage or safety risk is likely.";
        }
        if (diagnosis.getUrgency() == Urgency.HIGH) {
            return "Repair soon and avoid demanding driving conditions.";
        }
        if (diagnosis.getUrgency() == Urgency.MEDIUM) {
            return "Schedule repair before the next regular service.";
        }
        return "Monitor the situation and repeat the diagnostic scan if symptoms continue.";
    }
}
