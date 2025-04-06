package com.walkspring.entities;

import java.io.Serializable;

public class Checkin_PK implements Serializable {
    private Poi poi;
    private User user;

    public Checkin_PK() {
    }

    public Checkin_PK(Poi poi, User user) {
        this.poi = poi;
        this.user = user;
    }
}
