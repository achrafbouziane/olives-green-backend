package org.project.jobservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.jobservice.dto.JobVisitDTO;
import org.project.jobservice.entity.JobVisit;

@Mapper(componentModel = "spring")
public interface JobVisitMapper {

    @Mapping(target = "jobId", source = "job.id")
    JobVisitDTO toDTO(JobVisit visit);
}