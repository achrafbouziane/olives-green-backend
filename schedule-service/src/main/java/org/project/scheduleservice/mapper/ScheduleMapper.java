package org.project.scheduleservice.mapper;

import org.project.scheduleservice.dto.ScheduledJobDTO;
import org.project.scheduleservice.entity.ScheduledJob;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduledJobDTO mapToScheduledJobDTO(ScheduledJob scheduledJob);

}