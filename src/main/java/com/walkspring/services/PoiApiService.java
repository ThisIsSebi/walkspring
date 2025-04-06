package com.walkspring.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.walkspring.dtos.poi.PoiCollectionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoiApiService {

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    public PoiApiService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.webClient = webClientBuilder.baseUrl("https://de.wikipedia.org/w/api.php").build();
    }

    public void setWebClientToEnglish() {
        this.webClient = webClientBuilder.baseUrl("https://en.wikipedia.org/w/api.php").build();
    }

    public void setWebClientToGerman() {
        this.webClient = webClientBuilder.baseUrl("https://de.wikipedia.org/w/api.php").build();
    }

    public JsonNode fetchNearbyArticles(double lat, double lon, int radius) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("format", "json")
                            .queryParam("list", "geosearch")
                            .queryParam("gslimit", 50)
                            .queryParam("gscoord", lat + "|" + lon)
                            .queryParam("gsradius", radius)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public JsonNode fetchArticleById(String id) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("format", "json")
                            .queryParam("prop", "extracts|coordinates|info|pageimages")
                            .queryParam("pithumbsize", 500)
                            .queryParam("inprop", "url")
                            .queryParam("explaintext", "true")
                            .queryParam("exsentences", 5)
                            .queryParam("exintro", 1)
                            .queryParam("pageids", id)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public List<PoiCollectionDTO> collectNearbyArticles(double lat, double lon, int radius) {
        JsonNode nearbyArticlesNode = fetchNearbyArticles(lat, lon, radius);
        if (nearbyArticlesNode == null) {
            return List.of(); // Gibt eine leere, unveränderliche Liste zurück
        }

        JsonNode geosearchNode = nearbyArticlesNode.path("query").path("geosearch");
        if (!geosearchNode.isArray()) {
            return List.of();
        }

        List<PoiCollectionDTO> nearbyArticles = new ArrayList<>();
        geosearchNode.forEach(obj -> {
            if (obj.has("pageid") && obj.has("title") && obj.has("lat") && obj.has("lon")) {
                nearbyArticles.add(new PoiCollectionDTO(
                        obj.path("pageid").asText(),
                        obj.path("title").asText(),
                        obj.path("lat").asDouble(),
                        obj.path("lon").asDouble()
                ));
            }
        });

        return nearbyArticles;
    }

}
