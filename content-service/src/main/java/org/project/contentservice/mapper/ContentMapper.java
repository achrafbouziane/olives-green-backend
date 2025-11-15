package org.project.contentservice.mapper;

import org.project.contentservice.dto.ServicePageDTO;
import org.project.contentservice.entity.ServicePage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    ServicePageDTO mapToPageDTO(ServicePage page);

}
