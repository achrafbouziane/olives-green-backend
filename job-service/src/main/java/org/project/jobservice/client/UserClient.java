package org.project.jobservice.client;

import org.project.jobservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

// Ensure "user-service" matches the spring.application.name in user-service.yml
// Ensure the path matches your User Controller's RequestMapping
@FeignClient(name = "user-service", path = "/api/v1/admin/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") UUID id);
}