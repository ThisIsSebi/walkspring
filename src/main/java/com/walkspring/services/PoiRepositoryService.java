package com.walkspring.services;

import com.walkspring.dtos.poi.PoiCollectionDTO;
import com.walkspring.entities.Poi;
import com.walkspring.repositories.PoiCrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoiRepositoryService {

    private final PoiCrudRepository poiCrudRepository;

    public PoiRepositoryService(PoiCrudRepository poiCrudRepository) {
        this.poiCrudRepository = poiCrudRepository;
    }

    public List<Poi> getNearbyPois(double lat, double lon, int radius) {
        double rKm = radius / 1000.0;
        return poiCrudRepository.findPoisNearLocation(lat, lon, rKm);
    }

    public void saveOrUpdatePoi(Poi poi) {
        poiCrudRepository.save(poi);
    }

    public Poi getPoiByCoordinates(double lat, double lon) {
        return poiCrudRepository.findByLatitudeAndLongitude(lat, lon)
                .orElseThrow(() -> new RuntimeException("No Poi found with coordinates " + lat + ", " + lon));
    }

    public boolean existsByPageid(String pageid) {
        return poiCrudRepository.existsByPageid(pageid);
    }

    public void saveCollectionToDatabase(List<PoiCollectionDTO> collection) {
        if (collection.isEmpty()) {
            return; // Falls die Liste leer ist, nichts tun
        }

        List<Poi> newPois = new ArrayList<>();

        for (PoiCollectionDTO collectionItem : collection) {
            String pageId = collectionItem.getPageid();

            if (!poiCrudRepository.existsByPageid(pageId)) {
                newPois.add(new Poi(
                        pageId,
                        collectionItem.getTitle(),
                        collectionItem.getLatitude(),
                        collectionItem.getLongitude()
                ));
            }
        }

        if (!newPois.isEmpty()) {
            poiCrudRepository.saveAll(newPois);
        }
    }

}
/*
    private final String title;
    private final String body;
    private final double latitude;
    private final double longitude;
    private String url;
    private String imageUrl;
    private Image poiImage;
 */
