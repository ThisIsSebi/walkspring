package com.walkspring.enums;

import lombok.Getter;
import lombok.Setter;

public enum UserRole {

    ADMIN("Administrator"),
    MODERATOR("Moderator"),
    USER("User");

    private String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
