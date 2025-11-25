package org.project.contentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service_pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String pageSlug;

    private String title;
    private String subTitle;
    private String imageUrl;

    @Column(columnDefinition = "TEXT") // Allows for long paragraphs
    private String description;

    // This annotation automatically creates a side table to store the list items
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_page_features", joinColumns = @JoinColumn(name = "service_page_id"))
    @Column(name = "feature")
    private List<String> features;
}