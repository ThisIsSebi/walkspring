package com.walkspring.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walkspring.dtos.checkin.*;
import com.walkspring.entities.Poi;
import com.walkspring.entities.Checkin;
import com.walkspring.entities.User;
import com.walkspring.repositories.CheckinCrudRepository;
import com.walkspring.repositories.PoiCrudRepository;
import com.walkspring.repositories.UserCrudRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service

public class CheckinService {

    private final PoiCrudRepository poiCrudRepository;
    private final CheckinCrudRepository checkinCrudRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ObjectMapper jacksonObjectMapper;
    private final UserService userService;
    private final PoiService poiService;
    private final UserCrudRepository userCrudRepository;

    public CheckinService(PoiCrudRepository poiCrudRepository,
                          CheckinCrudRepository checkinCrudRepository,
                          AuthenticatedUserService authenticatedUserService, ObjectMapper jacksonObjectMapper, UserService userService, PoiService poiService, UserCrudRepository userCrudRepository) {
        this.poiCrudRepository = poiCrudRepository;
        this.checkinCrudRepository = checkinCrudRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.userService = userService;
        this.poiService = poiService;
        this.userCrudRepository = userCrudRepository;
    }

    public CheckinResponseDTO checkinToPoi(Authentication authentication, CheckinRequestDTO checkinRequest) {
        Poi poi = checkinFromDTO(checkinRequest).getPoi();
        String note = checkinRequest.getNote();
        boolean visible = checkinRequest.isVisible();

        User user = authenticatedUserService.getAuthenticatedUser(authentication);

        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        if (!checkinCrudRepository.existsByPoiAndUser(poi, user)) {
            Checkin checkin = new Checkin();
            checkin.setUser(user);
            checkin.setPoi(poi);
            checkin.setCheckinNote(note);
            checkin.setVisible(visible);
            checkinCrudRepository.save(checkin);
            return checkinToDTO(checkin);
        } else throw new RuntimeException("A checkin for this POI is already listed.");
    }

    public CheckinResponseDTO checkinToDTO(Checkin checkin) {

        CheckinPoiDTO checkinPoiDTO = checkinPoiToPoiDTO(checkin.getPoi());

        CheckinResponseDTO dto = new CheckinResponseDTO(
                checkinPoiDTO,
                checkin.getUser().getUserId(),
                checkin.getUser().getUsername(),
                checkin.getCheckinImage() != null ? checkin.getCheckinImage().getImageId() : 0,
                checkin.getCheckinNote() != null ? checkin.getCheckinNote() : "",
                checkin.getVisitedAt(),
                checkin.isVisible()
        );

        return dto;
    }

    public CheckinDeliveryDTO checkinToDelivery(Checkin checkin) {

        int userImageId = -1;

        if (checkin.getUser().getUserImage() != null) {
            if (checkin.getUser().getUserImage().getThumbnail() != null) {
                userImageId = checkin.getUser().getUserImage().getThumbnail().getImageId();
            } else {
                userImageId = checkin.getUser().getUserImage().getImageId();
            }
        }

        CheckinDeliveryDTO dto = new CheckinDeliveryDTO(
                checkin.getPoi().getPoiId(),
                checkin.getUser().getUserId(),
                checkin.getUser().getUsername(),
                userImageId,
                checkin.getCheckinNote()
        );

        return dto;
    }


    public CheckinPoiDTO checkinPoiToPoiDTO(Poi poi) {
        return new CheckinPoiDTO(poi.getPoiId(), poi.getTitle(), poi.getBody(), poi.getPoiImageUrl(), poi.getLatitude(), poi.getLongitude());
    }

    public Checkin checkinFromDTO(CheckinRequestDTO dto) {
        Poi poi = poiCrudRepository.findByLatitudeAndLongitude(dto.getLatitude(), dto.getLongitude())
                .orElseThrow(() -> new RuntimeException("Poi not found"));
        Checkin checkin = new Checkin(
                poi,
                dto.getNote(),
                dto.isVisible()
        );
        return checkin;
    }

    public JsonNode checkinToPoiRequest(Authentication authentication, CheckinRequestDTO checkinRequest) {
        CheckinResponseDTO response = checkinToPoi(authentication, checkinRequest);
        return jacksonObjectMapper.valueToTree(response);
    }

    public List<CheckinResponseDTO> findCheckinsOfUser(Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        List<Checkin> foundCheckins = checkinCrudRepository.findByUser(user);
        List<CheckinResponseDTO> userCheckins = new ArrayList<>();

        foundCheckins.forEach(checkin -> userCheckins.add(checkinToDTO(checkin)));

        return userCheckins;
    }

    //https://stackoverflow.com/questions/32269192/spring-no-entitymanager-with-actual-transaction-available-for-current-thread
    // Using @Transactional annotation to fix the error
    @Transactional
    public void deleteCheckinByPoiIdAndUser(String poiId, Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        Poi poi = poiCrudRepository.findById(Integer.valueOf(poiId))
                .orElseThrow(() -> new RuntimeException("Poi not found"));
        checkinCrudRepository.deleteByPoiAndUser(poi, user);
    }

    public Checkin findCheckinByPoiIdAndUser(int poiId, User user) {
        Poi poi = poiCrudRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("Poi not found."));
        Checkin checkin = checkinCrudRepository.findByPoiAndUser(poi, user)
                .orElseThrow(() -> new RuntimeException("Checkin not found."));

        return checkin;
    }

    public void deleteCheckinsOfActiveUser(Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        List<Checkin> foundCheckins = checkinCrudRepository.findByUser(user);
        checkinCrudRepository.deleteAll(foundCheckins);
    }

    public CheckinResponseDTO updateCheckin(
            String poiId,
            CheckinUpdateDTO checkinUpdate,
            Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Poi poi = poiCrudRepository.findById(Integer.valueOf(poiId))
                .orElseThrow(() -> new RuntimeException("Poi not found"));

        Checkin checkin = checkinCrudRepository.findByPoiAndUser(poi, user)
                .orElseThrow(() -> new RuntimeException("Checkin not found"));

        checkin.setCheckinNote(checkinUpdate.getCheckinNote());
        checkin.setVisible(checkinUpdate.isVisible());
//        HIER KOMMT NOCH DER SETTER FÜR DAS IMAGE HIN
        checkinCrudRepository.save(checkin);
        return checkinToDTO(checkin);
    }

//    ALT:
//    public void deleteCheckins(Authentication authentication, Double latitude, Double longitude) {
//        User user = authenticatedUserService.getAuthenticatedUser(authentication);
//
//        if (user == null && latitude!= null && longitude != null) {
//            Poi poi = poiCrudRepository.findByLatitudeAndLongitude(latitude, longitude)
//                    .orElseThrow(() -> new RuntimeException("Poi not found"));
//            List<Checkin> poiCheckins = checkinCrudRepository.findByPoi(poi);
//            checkinCrudRepository.deleteAll(poiCheckins);
//        }
//
//        if (latitude != null && longitude != null) {
//            Poi poi = poiCrudRepository.findByLatitudeAndLongitude(latitude, longitude)
//                    .orElseThrow(() -> new RuntimeException("POI not found"));
//            Checkin checkin = checkinCrudRepository.findByPoiAndUser(poi, user)
//                    .orElseThrow(() -> new RuntimeException("Checkin by POI and User not found."));
//            checkinCrudRepository.delete(checkin);
//        } else {
//            List<Checkin> userCheckins = checkinCrudRepository.findByUser(user);
//            checkinCrudRepository.deleteAll(userCheckins);
//        }
//    }

    public List<CheckinDeliveryDTO> findCheckinsOfPoi(int poiId) {
        Poi poi = poiCrudRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("Poi with ID " + poiId + " not found"));

        List<Checkin> foundCheckins = checkinCrudRepository.findByPoi(poi);
        List<CheckinDeliveryDTO> poiCheckins = new ArrayList<>();

        for (Checkin checkin : foundCheckins) {
            if (checkin.isVisible()) {
                poiCheckins.add(checkinToDelivery(checkin));
            }
        }
        System.out.println(poiCheckins);

        return poiCheckins;
    }

    public JsonNode ALTfindCheckinsOfPoi(double latitude, double longitude) {
        Poi poi = poiCrudRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseThrow(() -> new RuntimeException("Poi by coordinates not found"));

        List<Checkin> foundCheckins = checkinCrudRepository.findByPoi(poi);
        List<CheckinResponseDTO> poiCheckins = new ArrayList<>();

        for (Checkin checkin : foundCheckins) {
            if (checkin.isVisible()) {
                poiCheckins.add(checkinToDTO(checkin));
            }
        }

        return jacksonObjectMapper.valueToTree(poiCheckins);
    }

// ALT:
//    public JsonNode deleteCheckinsOfPoi(double lat, double lon) {
//        Poi poiOfCheckin = poiCrudRepository.findByLatitudeAndLongitude(lat, lon);
//        checkinCrudRepository.deleteByPoi(poiOfCheckin);
//    }

}

