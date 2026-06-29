package com.ftn.sbnz.model.events;

import java.util.Date;

public class CepAlert {

    private String type;
    private String vehicleId;
    private String message;
    private Date detectedAt;

    public CepAlert() {
    }

    public CepAlert(String type, String vehicleId, String message, Date detectedAt) {
        this.type = type;
        this.vehicleId = vehicleId;
        this.message = message;
        this.detectedAt = detectedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Date detectedAt) {
        this.detectedAt = detectedAt;
    }
}
