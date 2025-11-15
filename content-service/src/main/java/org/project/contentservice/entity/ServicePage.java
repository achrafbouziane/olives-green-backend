package org.project.contentservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_pages")
public class ServicePage {

    @Id
    @GeneratedValue
    private UUID id;

    // This is the URL key, e.g., "gardening" or "christmas-lights"
    @Column(nullable = false, unique = true)
    private String pageSlug;

    @Column(nullable = false)
    private String title;

    private String subTitle;

    private String imageUrl; // URL to the main image for this page

    @Column(columnDefinition = "TEXT")
    private String htmlContent; // The main text content for the page
}