package org.project.storageservice.service.impl;

import jakarta.annotation.PostConstruct;
import org.project.storageservice.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements StorageService {

    @Value("${storage.location}")
    private String location;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(location);
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Invalid path sequence " + originalFilename);
            }

            String filename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetLocation = this.rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/storage/files/")
                    .path(filename)
                    .toUriString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Path loadFile(String filename) {
        return rootLocation.resolve(filename);
    }
}