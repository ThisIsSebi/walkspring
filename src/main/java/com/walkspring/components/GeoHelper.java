package com.walkspring.components;

import org.springframework.stereotype.Component;

// Diese Klasse stellt Methoden zur Verfügung, die für geographische Berechnungen (Distanz!) wichtig sind.
@Component
public class GeoHelper {

    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        final int R = 6371; // Radius der Erde
        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}
