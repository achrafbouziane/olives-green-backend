package org.project.jobservice.controller;

import lombok.RequiredArgsConstructor;
import org.project.jobservice.entity.CatalogItem;
import org.project.jobservice.repository.CatalogItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogItemRepository repository;

    @GetMapping
    public ResponseEntity<List<CatalogItem>> getAllItems() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<CatalogItem> createItem(@RequestBody CatalogItem item) {
        return ResponseEntity.ok(repository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}