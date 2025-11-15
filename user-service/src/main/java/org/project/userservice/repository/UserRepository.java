package org.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.project.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByEmail(String email);
}
