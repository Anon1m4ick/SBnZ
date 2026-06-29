package com.ftn.sbnz.service;

import java.util.List;

public class BackwardVerificationResult {

    private final boolean confirmed;
    private final int matchingBindings;
    private final String queryName;

    public BackwardVerificationResult(boolean confirmed, int matchingBindings, String queryName) {
        this.confirmed = confirmed;
        this.matchingBindings = matchingBindings;
        this.queryName = queryName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getMatchingBindings() {
        return matchingBindings;
    }

    public String getQueryName() {
        return queryName;
    }
}
