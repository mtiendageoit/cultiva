package com.cultiva.webapp.field.images;

import java.time.LocalDate;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldImageRepository extends JpaRepository<FieldImage, Long> {
  List<FieldImage> findByUserId(long userId);
  List<FieldImage> findByFieldId(long fieldId);
  Optional<FieldImage> findByFieldIdAndFieldVersionAndIndiceIdAndImageDate(long fieldId, int version, int indice,
      LocalDate from);
}
