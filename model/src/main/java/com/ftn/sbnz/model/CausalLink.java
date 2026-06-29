package com.ftn.sbnz.model;

import org.kie.api.definition.type.Position;

public class CausalLink {

    @Position(0)
    private String cause;

    @Position(1)
    private String effect;

    public CausalLink() {
    }

    public CausalLink(String cause, String effect) {
        this.cause = cause;
        this.effect = effect;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
