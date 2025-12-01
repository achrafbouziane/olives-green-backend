package org.project.contentservice.repository;


import org.project.contentservice.entity.ServicePage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ServicePageRepository extends JpaRepository<ServicePage, UUID> {

    // This is the main way to find a page
    Optional<ServicePage> findByPageSlug(String pageSlug);
}