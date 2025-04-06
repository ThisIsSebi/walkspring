package com.walkspring.enums;
// ANMERKUNG: wird wahrscheinlich nicht gebraucht, da Enum bei zwei Zuständen nicht nötig
public enum CheckinStatus {

    DISCLOSED("Visible to all Users"),
    UNDISCLOSED("Visible to Creator only");

    private String status;

    private CheckinStatus(String status) {
        this.status = status;
    }

}
