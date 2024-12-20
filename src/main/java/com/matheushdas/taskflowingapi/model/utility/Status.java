package com.matheushdas.taskflowingapi.model.utility;

public enum Status {
    OPEN("IN_PROGRESS"), CLOSED("FINISHED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
