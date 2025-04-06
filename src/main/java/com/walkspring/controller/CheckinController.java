package com.walkspring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walkspring.dtos.checkin.CheckinDeliveryDTO;
import com.walkspring.dtos.checkin.CheckinRequestDTO;
import com.walkspring.dtos.checkin.CheckinResponseDTO;
import com.walkspring.dtos.checkin.CheckinUpdateDTO;
import com.walkspring.services.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/checkin")
@RequiredArgsConstructor
public class CheckinController {

    @Autowired
    private final CheckinService checkinService;

    @PostMapping()
    public ResponseEntity<JsonNode> checkin(Authentication authentication, @RequestBody CheckinRequestDTO checkinRequest) {
        try {
            JsonNode checkin = checkinService.checkinToPoiRequest(authentication, checkinRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(checkin);
        } catch (Exception e) {
            ObjectNode errorCheckin = new ObjectMapper().createObjectNode();
            errorCheckin.put("error", "Fehler: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorCheckin);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<CheckinResponseDTO>> checkinsOfUser(Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(checkinService.findCheckinsOfUser(authentication));
        } catch (Exception e) {
            ObjectNode errorCheckin = new ObjectMapper().createObjectNode();
            errorCheckin.put("error", "Fehler: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteCheckinsOfUser(Authentication authentication) {
        checkinService.deleteCheckinsOfActiveUser(authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{poiId}")
    public ResponseEntity<Void> deleteCheckins(@PathVariable String poiId, Authentication authentication) {
        checkinService.deleteCheckinByPoiIdAndUser(poiId, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/poi/{poiId}")
    public ResponseEntity<List<CheckinDeliveryDTO>> checkinsOfPoi(@PathVariable int poiId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(checkinService.findCheckinsOfPoi(poiId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ObjectNode errorCheckin = new ObjectMapper().createObjectNode();
            errorCheckin.put("error", "Fehler: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{poiId}")
    public ResponseEntity<CheckinResponseDTO> updateCheckin(
            @PathVariable String poiId,
            @RequestBody CheckinUpdateDTO checkinUpdate,
            Authentication authentication) {
        CheckinResponseDTO response = checkinService.updateCheckin(poiId, checkinUpdate, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
