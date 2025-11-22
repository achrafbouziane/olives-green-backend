package org.project.userservice.mapper;

import org.project.userservice.dto.UserDTO;
import org.project.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO mapToUserDTO(User user);
}