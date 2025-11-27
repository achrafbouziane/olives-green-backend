package org.project.storageservice.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
    String storeFile(MultipartFile file);
    Path loadFile(String filename);
}