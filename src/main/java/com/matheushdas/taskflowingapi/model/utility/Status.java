package com.matheushdas.taskflowingapi.model.utility;

public enum Status {
    PENDING("PENDING"), CLOSED("FINISHED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
