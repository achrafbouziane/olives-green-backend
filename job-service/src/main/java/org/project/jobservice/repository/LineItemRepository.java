package org.project.jobservice.repository;

import org.project.jobservice.entity.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LineItemRepository extends JpaRepository<LineItem, UUID> {
}