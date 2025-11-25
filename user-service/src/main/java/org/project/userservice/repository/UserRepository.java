package org.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.project.userservice.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByEmail(String email);

    User findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
