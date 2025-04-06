package com.walkspring.enums;

public enum ActivityStatus {

    INACTIVE("Inactive"),
    ACTIVE("Active");

    private String status;

    ActivityStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
