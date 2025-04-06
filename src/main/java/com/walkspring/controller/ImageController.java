package com.walkspring.controller;

import com.walkspring.repositories.CheckinCrudRepository;
import com.walkspring.repositories.ImageCrudRepository;
import com.walkspring.repositories.PoiCrudRepository;
import com.walkspring.services.AuthenticatedUserService;
import com.walkspring.services.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageCrudRepository imageCrudRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final PoiCrudRepository poiCrudRepository;
    private final CheckinCrudRepository checkinCrudRepository;
    private final ImageService imageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/uploads/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Controller funktioniert!");
    }

    @GetMapping("/test-upload")
    public ResponseEntity<String> testAlternativeEndpoint() {
        return ResponseEntity.ok("Alternative Mapping funktioniert!");
    }


    @PostMapping("/checkin/{poiId}")
    public ResponseEntity<String> uploadCheckinImage(@RequestParam("file") MultipartFile file,
                                                     @PathVariable int poiId,
                                                     Authentication authentication) {
        try {
            String filePath = imageService.saveImage(file, poiId, authentication);
            imageService.saveCheckinImage(filePath, poiId, authentication);
            return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        } catch (IllegalArgumentException e) {
            log.error("Error uploading image", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            log.error("Error uploading image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @PostMapping("/poi/{poiId}")
    public ResponseEntity<String> uploadPoiImage(@RequestParam("file") MultipartFile file,
                                                 @PathVariable int poiId,
                                                 Authentication authentication) {
        try {
            String filePath = imageService.saveImage(file, poiId, authentication);
            imageService.savePoiImage(filePath, poiId);
            return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        } catch (IOException e) {
            log.error("Error uploading image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @PostMapping("/user")
    public ResponseEntity<String> uploadUserImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String filePath = imageService.saveImage(file, 0, authentication);
            imageService.saveUserImage(filePath, authentication);
            return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        } catch (IOException e) {
            log.error("Error uploading image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @PutMapping("/user")
    public ResponseEntity<String> updateUserImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String filePath = imageService.saveImage(file, 0, authentication);
            imageService.updateUserImage(filePath, authentication);
            return ResponseEntity.ok("Image updated successfully: " + filePath);
        } catch (IOException e) {
            log.error("Error updating image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating image");
        }
    }

    @GetMapping("checkin/{poiId}")
    public byte[] getCheckinImage(@PathVariable int poiId, Authentication authentication) throws IOException {
       return imageService.getCheckinImageByPoiAndUser(poiId, authentication);
    }

    @GetMapping("/{imageId}")
    public byte[] getImage(@PathVariable int imageId) throws IOException {
        return imageService.getImage(imageId);
    }

    @GetMapping("/user")
    public List<String> getAllImages(Authentication authentication){
        List<String> images = imageService.getAllImages(authentication);
        return images;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleImageNotFoundException(IllegalStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}