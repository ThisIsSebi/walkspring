package com.walkspring.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walkspring.dtos.poi.PoiCollectionDTO;
import com.walkspring.dtos.poi.PoiCustomDTO;
import com.walkspring.dtos.poi.PoiDeliveryDTO;
import com.walkspring.entities.Poi;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PoiService {

    private final PoiApiService poiApiService;
    private final PoiRepositoryService poiRepositoryService;
    private final ObjectMapper objectMapper;

    public PoiService(PoiApiService poiApiService, PoiRepositoryService poiRepositoryService, ObjectMapper objectMapper) {
        this.poiApiService = poiApiService;
        this.poiRepositoryService = poiRepositoryService;
        this.objectMapper = objectMapper;
    }

    private <T> T getRandomElement(List<T> list) {
        return list.isEmpty() ? null : list.get((int) (Math.random() * list.size()));
    }

    private boolean isPoiRecent(Poi poi) {
        return poi.getUpdatedAt() != null && poi.getUpdatedAt().isBefore(Instant.now().minusSeconds(48 * 3600));
    }

    private boolean isPoiComplete(Poi poi) {
        return poi.getTitle() != null && !poi.getTitle().isEmpty()
                && poi.getBody() != null && !poi.getBody().isEmpty()
                && poi.getPoiImage() != null
                && poi.getLatitude() != 0
                && poi.getLongitude() != 0
                && poi.getPoiImageUrl() != null && !poi.getPoiImageUrl().isEmpty();
    }

    public JsonNode findOrFetchPoi(double lat, double lon, int radius) {
        List<Poi> nearbyPois = poiRepositoryService.getNearbyPois(lat, lon, radius);
        int maxAttempts = 5;
        int attempts = 0;
        do {
            List<PoiCollectionDTO> collectedArticles = poiApiService.collectNearbyArticles(lat, lon, radius);
            if (nearbyPois.size() < 4) {
                radius *= 1.5;
                attempts++;
            }
            if (!collectedArticles.isEmpty()) {
                poiRepositoryService.saveCollectionToDatabase(collectedArticles);
                nearbyPois = poiRepositoryService.getNearbyPois(lat, lon, radius);
            }
        } while (nearbyPois.size() < 4 && attempts < maxAttempts);

        Poi poi = getRandomFromList(nearbyPois);

        if (poi == null) {
            throw new RuntimeException("No POI was found after " + attempts + " attempts.");
        }

        return objectMapper.valueToTree(poiToDelivery(fetchAndEnrichPoi(poi)));
    }


    private Poi fetchAndEnrichPoi(Poi poi) {
        if ((isPoiComplete(poi) && isPoiRecent(poi)) || poi.isUserGenerated()) {
            return poi;
        }
        JsonNode articleData = poiApiService.fetchArticleById(poi.getPageid());
        if (articleData != null) {
            updatePoiWithArticleData(poi, articleData);
            poiRepositoryService.saveOrUpdatePoi(poi);
        }
        return poi;
    }

    private void updatePoiWithArticleData(Poi poi, JsonNode articleData) {
        JsonNode pageNode = articleData.path("query").path("pages").path(poi.getPageid());
        poi.setTitle(pageNode.path("title").asText());
        poi.setBody(pageNode.path("extract").asText());
        poi.setPoiImageUrl(pageNode.path("thumbnail").path("source").asText());
        poi.setUrl(pageNode.path("canonicalurl").asText());
    }


    private Poi getRandomFromList(List<Poi> pois) {
        return getRandomElement(pois);
    }

    public JsonNode fetchEnglishPoi(double lat, double lon, int radius) {
        List<PoiCollectionDTO> englishCollection = new ArrayList<>();
        int maxAttempts = 5;
        int attempts = 0;

        while (englishCollection.size() < 4 && attempts < maxAttempts) {
            List<PoiCollectionDTO> collectedArticles = poiApiService.collectNearbyArticles(lat, lon, radius);
            englishCollection.addAll(collectedArticles);

            if (englishCollection.size() < 4) {
                radius *= 1.5;
                attempts++;
            }
        }
        if (englishCollection == null) {
            throw new RuntimeException("No POI was found after " + attempts + " attempts.");
        }
        PoiCollectionDTO finalCollect = getRandomElement(englishCollection);
        Poi englishPoi = new Poi(
                finalCollect.getPageid(),
                finalCollect.getTitle(),
                finalCollect.getLatitude(),
                finalCollect.getLongitude()
        );
        JsonNode articleData = poiApiService.fetchArticleById(englishPoi.getPageid());
        updatePoiWithArticleData(englishPoi, articleData);
        return objectMapper.valueToTree(poiToDelivery(englishPoi));
    }


    public PoiDeliveryDTO poiToDelivery(Poi poi) {
        return new PoiDeliveryDTO(
                poi.getPoiId(),
                poi.getTitle(),
                poi.getPageid() != null ? poi.getPageid() : "",
                poi.getBody(),
                poi.getPoiImage() != null ? poi.getPoiImage().getImageId() : 0,
                poi.getLatitude(),
                poi.getLongitude(),
                poi.getUrl() != null ? poi.getUrl() : "",
                poi.getPoiImageUrl() != null ? poi.getPoiImageUrl() : ""
        );
    }

    public PoiDeliveryDTO createNewPoi(PoiCustomDTO poiToCreate) {
        Poi poi = new Poi(
                poiToCreate.getTitle(),
                poiToCreate.getBody(),
                poiToCreate.getLatitude(),
                poiToCreate.getLongitude(),
                poiToCreate.getUrl()
        );
        poiRepositoryService.saveOrUpdatePoi(poi);
        return poiToDelivery(poi);
    }

    public PoiDeliveryDTO updatePoiByCoordinates(double lat, double lon, PoiCustomDTO poi) {
        Poi poiToUpdate = poiRepositoryService.getPoiByCoordinates(lat, lon);

        poiToUpdate.setBody(poi.getBody());
        poiToUpdate.setTitle(poi.getTitle());
        poiToUpdate.setLatitude(poi.getLatitude());
        poiToUpdate.setLongitude(poi.getLongitude());
        poiToUpdate.setUrl(poi.getUrl());

        poiRepositoryService.saveOrUpdatePoi(poiToUpdate);
        return poiToDelivery(poiToUpdate);
    }

    public JsonNode hardcodedPoi() {
        PoiDeliveryDTO hardcodedPoi = new PoiDeliveryDTO(
                29,
                "Coders.Bay",
                "SW2.9",
                "Die Coders.Bay Vienna ist ein wunderschöner Ort im Herzen des Gasometers. Sie ist bekannt für großartigen Kaffee, ein herrliches Panorama und gesundes Essen. Besondere Aufmerksamkeit erhielt die Coders.Bay im Feber 2025, als die Software-Gruppe 2.9 ihre hervorragend konzipierten und definitiv bug-befreiten Projekte präsentierte. Hugo++!",
                666,
                48.18487979280497,
                16.421012727226096,
                "https://www.codersbay.wien",
                "https://cdn.vuetifyjs.com/images/cards/sunshine.jpg"
        );

        Poi coderspoi = new Poi(
                hardcodedPoi.getPageId(),
                hardcodedPoi.getTitle(),
                hardcodedPoi.getLatitude(),
                hardcodedPoi.getLongitude()
        );
        Poi testpoi = new Poi("Poi-ID", "Poi of Interest", 48.2012, 16.1912);
        poiRepositoryService.saveOrUpdatePoi(testpoi);
        poiRepositoryService.saveOrUpdatePoi(coderspoi);
        System.out.println(testpoi.toString());

        return objectMapper.valueToTree(hardcodedPoi);
    }

//    public JsonNode createNewPoi(JsonNode poiToCreate) {
//        Poi newPoi = new Poi(
//                poiToCreate.path("title").asText(),
//                poiToCreate.path("body").asText(),
//                poiToCreate.path("latitude").asDouble(),
//                poiToCreate.path("longitude").asDouble(),
//                poiToCreate.path("image").asInt(),
//                poiToCreate.path("url").asText()
//        );
//
//        poiRepositoryService.saveOrUpdatePoi(newPoi); // Speichern in die DB
//
//        return objectMapper.valueToTree(newPoi); // Als JSON zurückgeben
//    }

}
