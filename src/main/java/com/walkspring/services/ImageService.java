package com.walkspring.services;

import com.walkspring.dtos.images.ImageDTO;
import com.walkspring.entities.*;
import com.walkspring.repositories.CheckinCrudRepository;
import com.walkspring.repositories.ImageCrudRepository;
import com.walkspring.repositories.PoiCrudRepository;
import com.walkspring.repositories.UserCrudRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageCrudRepository imageCrudRepository;
    private final UserService userService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PoiCrudRepository poiCrudRepository;
    private final UserCrudRepository userCrudRepository;
    private final CheckinCrudRepository checkinCrudRepository;
    private final CheckinService checkinService;


    @Value("${file.upload-dir}")
    private String uploadDir;


    // IMAGE SPEICHERN

    public boolean validFileFormat(MultipartFile file) {
        String contentType = file.getContentType();
        int maxFileSize = 50 * 1024 * 1024;

        if (file.isEmpty() || file.getSize() > maxFileSize) {
            return false;
        }

        if (contentType == null ||
                (!contentType.equalsIgnoreCase("image/jpeg") && !contentType.equalsIgnoreCase("image/png"))) {
            return false;
        }
        return true;
    }


    public String saveImage(MultipartFile file, int id, Authentication authentication) throws IOException {
        if (!validFileFormat(file)) {
            throw new IllegalArgumentException("Invalid file format. Only JPEG or PNG files with less than 2MB are supported.");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        User user = authenticatedUserService.getAuthenticatedUser(authentication);

        String filename = id + "_" + user.getUserId() + "_" + user.getUsername() + "_" + file.getOriginalFilename().replaceAll("[\\s/\\\\]+", "_");
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    // THUMBNAIL SPEICHERN

    private String generateThumbnail(String originalImagePath) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(originalImagePath));

        BufferedImage thumbnail = Thumbnails.of(originalImage)
                .size(150, 150)
                .asBufferedImage();

        String thumbnailPath = originalImagePath.replace(".jpg", "_thumb.jpg").replace(".png", "_thumb.png");

        ImageIO.write(thumbnail, "jpg", new File(thumbnailPath));

        return thumbnailPath;
    }

    // IMAGE FÜR CHECKIN SPEICHERN

    public void saveCheckinImage(String filepath, int poiId, Authentication authentication) {
        Image image = new Image();
        image.setPath(filepath);
        User user = authenticatedUserService.getAuthenticatedUser(authentication);

        Poi poi = poiCrudRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("Poi with id " + poiId + " not found"));

        Checkin checkin = checkinCrudRepository.findById(new Checkin_PK(poi, user))
                .orElseThrow(() -> new RuntimeException("Checkin with id " + poiId + " not found"));

        imageCrudRepository.save(image);
        checkin.setCheckinImage(image);
        checkinCrudRepository.save(checkin);
    }

    // IMAGE FÜR POI SPEICHERN

    public void savePoiImage(String filepath, int poiId) {
        Image image = new Image();
        image.setPath(filepath);
        Poi poi = poiCrudRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("Poi with id " + poiId + " not found"));

        imageCrudRepository.save(image);
        poi.setPoiImage(image);
        poiCrudRepository.save(poi);
    }

    // IMAGE FÜR USER SPEICHERN

    public void saveUserImage(String filepath, Authentication authentication) throws IOException {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        Image image = new Image();
        image.setPath(filepath);
        String thumbnailPath = generateThumbnail(filepath);

        Image thumbnail = new Image();
        thumbnail.setPath(thumbnailPath);
        image.setThumbnail(thumbnail);

        imageCrudRepository.save(thumbnail);
        imageCrudRepository.save(image);
        user.setUserImage(image);
        userCrudRepository.save(user);
    }

    // IMAGE FÜR USER UPDATEN/NEU SPEICHERN

    public void updateUserImage(String filepath, Authentication authentication) throws IOException {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);

        if (user.getUserImage() != null) {
            imageCrudRepository.deleteById(user.getUserImage().getImageId());
        }

        saveUserImage(filepath, authentication);
    }

    // IMAGE AUSHEBEN

    public byte[] getImage(int imageId) throws IOException {
        Image image = imageCrudRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image with id " + imageId + " not found"));

        File file = new File(image.getPath());

        return Files.readAllBytes(file.toPath());
    }

    // THUMBNAIL AUSGEBEN

    /*public byte[] getThumbnail(int imageId) throws IOException {
        Image image = imageCrudRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image with id " + imageId + " not found"));

        if (image.getThumbnail() == null) {
            return null;
        }

        Image thumbnail = image.getThumbnail();
        File file = new File(thumbnail.getPath());
        return Files.readAllBytes(file.toPath());
    }*/

    public byte[] getCheckinImageByPoiAndUser(int poiId, Authentication authentication) throws IOException {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        Checkin checkin = checkinService.findCheckinByPoiIdAndUser(poiId, user);
        if (checkin == null || checkin.getCheckinImage() == null) {
            throw new IllegalStateException("Kein Bild für diesen Check-in gefunden.");
        }
        return getImage(checkin.getCheckinImage().getImageId());
    }

    public List<String> getAllImages(Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        List<Checkin> checkins = checkinCrudRepository.findByUser(user);
        List<String> images = new ArrayList<>();

        for (Checkin checkin : checkins) {
            if (checkin.getCheckinImage() != null) {
                try {
                    File file = new File(checkin.getCheckinImage().getPath());
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String base64 = Base64.getEncoder().encodeToString(fileContent);
                    images.add("data:image/jpeg;base64," + base64); // Direkt im <img> nutzbar
                } catch (IOException e) {
                    e.printStackTrace(); // Besser Logging verwenden
                }
            }
        }
        return images;
    }

}
