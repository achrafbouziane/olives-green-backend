package org.project.userservice.service;

import org.project.userservice.dto.RegisterRequest;
import org.project.userservice.dto.UpdateUserRequest;
import org.project.userservice.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(RegisterRequest request);
    UserDTO updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
    List<UserDTO> getAllUsers();

}
