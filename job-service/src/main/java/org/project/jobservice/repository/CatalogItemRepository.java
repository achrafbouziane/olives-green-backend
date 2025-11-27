package org.project.jobservice.repository;

import org.project.jobservice.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, UUID> {
}