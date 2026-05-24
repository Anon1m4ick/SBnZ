package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.enums.ActionType;
import com.ftn.sbnz.model.enums.Component;
import com.ftn.sbnz.model.enums.CostRange;
import com.ftn.sbnz.model.enums.ServiceType;
import com.ftn.sbnz.model.enums.Urgency;

public class Recommendation {
    private ActionType action;
    private ServiceType serviceType;
    private CostRange costRange;
    private Urgency urgency;
    private String warningText;
    private List<Component> components = new ArrayList<>();

    public Recommendation() {
    }

    public Recommendation(ActionType action, ServiceType serviceType, CostRange costRange, Urgency urgency,
            String warningText, List<Component> components) {
        this.action = action;
        this.serviceType = serviceType;
        this.costRange = costRange;
        this.urgency = urgency;
        this.warningText = warningText;
        this.components = components;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public CostRange getCostRange() {
        return costRange;
    }

    public void setCostRange(CostRange costRange) {
        this.costRange = costRange;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public String getWarningText() {
        return warningText;
    }

    public void setWarningText(String warningText) {
        this.warningText = warningText;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
