package com.ftn.sbnz.model;

import com.ftn.sbnz.model.enums.DTCStatus;
import com.ftn.sbnz.model.enums.VehicleSystem;

public class DTCCode {
    private String code;
    private DTCStatus status;
    private VehicleSystem subsystem;

    public DTCCode() {
    }

    public DTCCode(String code, DTCStatus status, VehicleSystem subsystem) {
        this.code = code;
        this.status = status;
        this.subsystem = subsystem;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DTCStatus getStatus() {
        return status;
    }

    public void setStatus(DTCStatus status) {
        this.status = status;
    }

    public VehicleSystem getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(VehicleSystem subsystem) {
        this.subsystem = subsystem;
    }
}
