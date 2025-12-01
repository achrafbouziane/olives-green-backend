package org.project.contentservice.controller;


import org.project.contentservice.dto.SavePageRequest;
import org.project.contentservice.dto.ServicePageDTO;
import org.project.contentservice.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // --- PUBLIC ENDPOINTS ---
    // (Used by your public-site React app)

    @GetMapping("/pages")
    public ResponseEntity<List<ServicePageDTO>> getAllPages() {
        return ResponseEntity.ok(contentService.getAllPages());
    }

    @GetMapping("/pages/{slug}")
    public ResponseEntity<ServicePageDTO> getPageBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(contentService.getPageBySlug(slug));
    }

    // --- ADMIN ENDPOINTS ---
    // (Used by your admin-panel React app, protected by the Gateway)

    @PostMapping("/pages")
    public ResponseEntity<ServicePageDTO> createPage(@RequestBody SavePageRequest request) {
        ServicePageDTO page = contentService.createPage(request);
        return new ResponseEntity<>(page, HttpStatus.CREATED);
    }

    @PutMapping("/pages/{slug}")
    public ResponseEntity<ServicePageDTO> updatePage(
            @PathVariable String slug,
            @RequestBody SavePageRequest request) {
        ServicePageDTO page = contentService.updatePage(slug, request);
        return ResponseEntity.ok(page);
    }
}