package com.cultiva.webapp.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  Optional<User> findByEmail(String email);
  Optional<User> findByResetPasswordToken(String token);
  Optional<User> findByConfirmationToken(String token);
}
