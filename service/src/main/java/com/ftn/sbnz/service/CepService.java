package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.events.CepAlert;
import com.ftn.sbnz.model.events.DtcEvent;
import com.ftn.sbnz.model.events.SensorEvent;

@Service
public class CepService {

    private static final String DEMO_VEHICLE_ID = "VIN-CEP-DEMO";

    private final KieBase cepKieBase;

    public CepService(KieBase cepKieBase) {
        this.cepKieBase = cepKieBase;
    }

    public List<CepAlert> runOverheatingDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint obdStream = session.getEntryPoint("obdStream");
            SessionPseudoClock clock = session.getSessionClock();
            double[] coolantReadings = { 95.0, 100.0, 106.0, 112.0, 118.0 };
            for (double reading : coolantReadings) {
                obdStream.insert(sensorEvent("coolant_temp", reading, clock));
                clock.advanceTime(60, TimeUnit.SECONDS);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    public List<CepAlert> runVoltageOscillationDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint obdStream = session.getEntryPoint("obdStream");
            SessionPseudoClock clock = session.getSessionClock();
            double[] voltages = { 13.0, 14.8, 12.5, 14.5 };
            for (double voltage : voltages) {
                obdStream.insert(sensorEvent("battery_voltage", voltage, clock));
                clock.advanceTime(30, TimeUnit.SECONDS);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    public List<CepAlert> runSporadicMisfireDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint dtcStream = session.getEntryPoint("dtcStream");
            SessionPseudoClock clock = session.getSessionClock();
            String[] codes = { "P0301", "P0302", "P0303" };
            for (String code : codes) {
                dtcStream.insert(new DtcEvent(DEMO_VEHICLE_ID, code, new Date(clock.getCurrentTime())));
                clock.advanceTime(2, TimeUnit.MINUTES);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    public List<CepAlert> runMafDecliningDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint obdStream = session.getEntryPoint("obdStream");
            SessionPseudoClock clock = session.getSessionClock();
            double[] mafReadings = { 15.0, 13.4, 11.2, 9.6, 8.8 };
            for (double reading : mafReadings) {
                obdStream.insert(sensorEvent("maf", reading, clock));
                clock.advanceTime(60, TimeUnit.SECONDS);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    public List<CepAlert> runTyrePressureDropDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint obdStream = session.getEntryPoint("obdStream");
            SessionPseudoClock clock = session.getSessionClock();
            double[] pressures = { 2.35, 2.26, 2.04, 1.92 };
            for (double pressure : pressures) {
                obdStream.insert(sensorEvent("tyre_pressure", pressure, clock));
                clock.advanceTime(45, TimeUnit.SECONDS);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    public List<CepAlert> runRepeatedCriticalWarningsDemo() {
        KieSession session = newPseudoClockSession();
        try {
            EntryPoint dtcStream = session.getEntryPoint("dtcStream");
            SessionPseudoClock clock = session.getSessionClock();
            String[] codes = { "P0217", "P0524" };
            for (String code : codes) {
                dtcStream.insert(new DtcEvent(DEMO_VEHICLE_ID, code, new Date(clock.getCurrentTime())));
                clock.advanceTime(4, TimeUnit.MINUTES);
                session.fireAllRules();
            }
            return collectAlerts(session);
        } finally {
            session.dispose();
        }
    }

    private KieSession newPseudoClockSession() {
        KieSessionConfiguration configuration = KieServices.Factory.get().newKieSessionConfiguration();
        configuration.setOption(ClockTypeOption.get("pseudo"));
        return cepKieBase.newKieSession(configuration, null);
    }

    private SensorEvent sensorEvent(String name, double value, SessionPseudoClock clock) {
        return new SensorEvent(DEMO_VEHICLE_ID, name, value, new Date(clock.getCurrentTime()));
    }

    @SuppressWarnings("unchecked")
    private List<CepAlert> collectAlerts(KieSession session) {
        Collection<?> alerts = session.getObjects(new ClassObjectFilter(CepAlert.class));
        List<CepAlert> result = new ArrayList<>();
        for (Object alert : alerts) {
            result.add((CepAlert) alert);
        }
        return result;
    }
}
