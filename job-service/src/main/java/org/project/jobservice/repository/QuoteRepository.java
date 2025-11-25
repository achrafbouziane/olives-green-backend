package org.project.jobservice.repository;

import org.project.jobservice.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    List<Quote> findByCustomerId(UUID customerId);
}