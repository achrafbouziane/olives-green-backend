package org.project.userservice.service;

import org.project.userservice.dto.RegisterRequest;
import org.project.userservice.dto.UserDTO;

public interface UserService {
    UserDTO createUser(RegisterRequest request);
}
