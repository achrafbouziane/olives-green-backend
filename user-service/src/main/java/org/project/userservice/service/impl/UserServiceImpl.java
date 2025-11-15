package org.project.userservice.service.impl;

import org.project.userservice.dto.RegisterRequest;
import org.project.userservice.dto.UserDTO;
import org.project.userservice.entity.User;
import org.project.userservice.mapper.UserMapper;
import org.project.userservice.repository.UserRepository;
import org.project.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        var savedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(savedUser);
    }


}