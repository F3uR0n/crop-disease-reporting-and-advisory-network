package com.cdran.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Saves uploaded case photos as files in the upload folder (app.upload.dir, default "uploads/").
 * Only the returned relative path is stored in the database (CaseImage.filePath).
 * Files get a random UUID name, so two uploads never overwrite each other and names cannot be guessed.
 * WebConfig serves the folder at /uploads/** for logged-in users.
 */
@Service
public class ImageStorageService {

    private final Path uploadRoot;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath();
    }

    /** Stores the file and returns its path relative to the upload folder, e.g. "cases/3f2a....jpg". */
    public String store(MultipartFile file) {
        String extension = "image/png".equals(file.getContentType()) ? ".png" : ".jpg";
        String relativePath = "cases/" + UUID.randomUUID() + extension;
        Path target = uploadRoot.resolve(relativePath);
        try (InputStream in = file.getInputStream()) {
            Files.createDirectories(target.getParent());
            Files.copy(in, target);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save uploaded image", e);
        }
        return relativePath;
    }
}
