package org.project.jobservice.client;

import org.project.jobservice.dto.CreateScheduleRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @PostMapping("/api/v1/schedules")
    void createSchedule(@RequestBody CreateScheduleRequest request);
}