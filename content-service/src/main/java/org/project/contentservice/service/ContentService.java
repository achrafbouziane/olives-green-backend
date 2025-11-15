package org.project.contentservice.service;


import org.project.contentservice.dto.SavePageRequest;
import org.project.contentservice.dto.ServicePageDTO;
import java.util.List;

public interface ContentService {

    /**
     * Gets a single page by its slug (e.g., "gardening").
     */
    ServicePageDTO getPageBySlug(String slug);

    /**
     * Gets a list of all available pages.
     */
    List<ServicePageDTO> getAllPages();

    /**
     * Creates a new page.
     */
    ServicePageDTO createPage(SavePageRequest request);

    /**
     * Updates an existing page, finding it by its slug.
     */
    ServicePageDTO updatePage(String slug, SavePageRequest request);
}