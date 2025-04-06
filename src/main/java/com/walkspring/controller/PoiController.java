package com.walkspring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walkspring.dtos.poi.PoiCustomDTO;
import com.walkspring.dtos.poi.PoiDeliveryDTO;
import com.walkspring.repositories.PoiCrudRepository;
import com.walkspring.services.PoiApiService;
import com.walkspring.services.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class PoiController {

    @Autowired
    private PoiService poiService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PoiApiService poiApiService;
    @Autowired
    private PoiCrudRepository poiCrudRepository;

    @GetMapping("/codersbay")
    public ResponseEntity<JsonNode> getCodersbay() {
        try {
            JsonNode hardJason = poiService.hardcodedPoi();
            return ResponseEntity.ok(hardJason);
        } catch (Exception e) {
            ObjectNode errorJson = new ObjectMapper().createObjectNode();
            errorJson.put("error", "Fehler: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorJson);
        }
    }

    @GetMapping("/random")
    public ResponseEntity<JsonNode> getRandom(@RequestParam double lat, @RequestParam double lon, @RequestParam int radius) {
        try {
            JsonNode randomJson = poiService.findOrFetchPoi(lat, lon, radius);
            return ResponseEntity.ok(randomJson);
        } catch (Exception e) {
            ObjectNode errorJson = new ObjectMapper().createObjectNode();
            errorJson.put("error", "Fehler: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorJson);
        }
    }

    @GetMapping("/random/en")
    public ResponseEntity<JsonNode> getRandomEnglish(@RequestParam double lat, @RequestParam double lon, @RequestParam int radius) {
        try {
            poiApiService.setWebClientToEnglish();
            JsonNode randomJson = poiService.fetchEnglishPoi(lat, lon, radius);
            poiApiService.setWebClientToGerman();
            return ResponseEntity.ok(randomJson);
        } catch (Exception e) {
            ObjectNode errorJson = new ObjectMapper().createObjectNode();
            errorJson.put("error", "Fehler: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorJson);
        }
    }

    @PostMapping("/poi")
    public ResponseEntity<PoiDeliveryDTO> createNewPoi(@RequestBody PoiCustomDTO poi) {
        return ResponseEntity.status(HttpStatus.CREATED).body(poiService.createNewPoi(poi));
    }

    @DeleteMapping("/{poiId}")
    public ResponseEntity<String> delete(@PathVariable int poiId) {
        poiCrudRepository.deletePoiByPoiId(poiId);
        return ResponseEntity.status(HttpStatus.OK).body("POI deleted.");
    }

}
