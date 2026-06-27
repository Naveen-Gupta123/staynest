package com.staynest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FileStorageUtil - handles saving uploaded images (property photos,
 * profile pictures) to disk and returning a URL path the browser can use.
 *
 * WHY A SEPARATE UTIL CLASS (not inline in the controller)?
 * File storage logic is a cross-cutting concern used by BOTH the
 * Property module (listing photos) and the Profile module (profile
 * pictures). Putting it in one reusable class avoids duplicating the
 * same "generate unique filename, write bytes to disk" logic twice.
 *
 * NOTE FOR PRODUCTION/DEPLOYMENT: storing files on the local disk works
 * for a college project and even a small live deployment, but on most
 * cloud hosts (Render, Railway, Heroku) the filesystem is EPHEMERAL -
 * uploaded files disappear on every redeploy/restart. For a real
 * "Airbnb-scale" business, swap this class's internals for an AWS S3 /
 * Cloudinary upload call - the rest of the app (which only ever sees a
 * URL string) doesn't need to change at all. That's the benefit of
 * isolating this logic here.
 */
@Component
public class FileStorageUtil {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Saves an uploaded file with a random unique name (to avoid
     * filename collisions between different users uploading
     * "photo.jpg" at the same time) and returns the relative URL.
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String uniqueName = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath);

            // This is the URL the browser will request - mapped to the
            // physical uploadDir folder via WebConfig's resource handler.
            return "/uploads/" + uniqueName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }
}
