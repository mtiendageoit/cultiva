package com.cultiva.webapp.field;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Long> {
  List<Field> findAllByUserId(long userId);
  void deleteByUuid(String uuid);
  void deleteByUserId(long userId);
  Optional<Field> findByUuid(String uuid);
}
