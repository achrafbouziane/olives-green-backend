package org.project.contentservice.service.impl;

import org.project.contentservice.dto.SavePageRequest;
import org.project.contentservice.dto.ServicePageDTO;
import org.project.contentservice.mapper.ContentMapper;
import org.project.contentservice.entity.ServicePage;
import org.project.contentservice.repository.ServicePageRepository;
import org.project.contentservice.service.ContentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ServicePageRepository pageRepository;
    private final ContentMapper contentMapper;

    @Override
    public ServicePageDTO getPageBySlug(String slug) {
        ServicePage page = findBySlug(slug);
        return contentMapper.toDTO(page);
    }

    @Override
    public List<ServicePageDTO> getAllPages() {
        return pageRepository.findAll().stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServicePageDTO createPage(SavePageRequest request) {
        // Check if slug already exists
        if (pageRepository.findByPageSlug(request.pageSlug()).isPresent()) {
            throw new IllegalArgumentException("Page slug already exists: " + request.pageSlug());
        }

        // Use Mapper to create Entity
        ServicePage page = contentMapper.toEntity(request);

        ServicePage savedPage = pageRepository.save(page);
        return contentMapper.toDTO(savedPage);
    }

    @Override
    @Transactional
    public ServicePageDTO updatePage(String slug, SavePageRequest request) {
        ServicePage page = findBySlug(slug);

        // Option A: Use Mapper to update fields (cleanest)
        // ensure your mapper ignores 'id' and 'pageSlug' if you don't want them changed
        // contentMapper.updateEntityFromRequest(request, page);

        // Option B: Manual setters (Safest for specific business rules)
        page.setTitle(request.title());
        page.setSubTitle(request.subTitle());
        page.setImageUrl(request.imageUrl());
        page.setDescription(request.description());
        page.setFeatures(request.features());

        ServicePage savedPage = pageRepository.save(page);
        return contentMapper.toDTO(savedPage);
    }

    // --- Private Helpers ---

    private ServicePage findBySlug(String slug) {
        return pageRepository.findByPageSlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Page not found with slug: " + slug));
    }
}