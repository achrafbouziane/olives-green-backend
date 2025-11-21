package org.project.contentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.contentservice.dto.SavePageRequest;
import org.project.contentservice.dto.ServicePageDTO;
import org.project.contentservice.entity.ServicePage;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    // Entity to DTO
    ServicePageDTO toDTO(ServicePage servicePage);

    // Request to Entity (for creation)
    ServicePage toEntity(SavePageRequest request);

    // Request to Entity (for update)
    // We ignore id and pageSlug during update to prevent accidental changes
    // or you can handle them manually in the service
    void updateEntityFromRequest(SavePageRequest request, @MappingTarget ServicePage entity);
}