package org.project.userservice.mapper;

import org.project.userservice.dto.UserDTO;
import org.project.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // MapStruct can't convert UUID to String automatically, so we help it.
    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserDTO mapToUserDTO(User user);
}