package com.cultiva.webapp.field.images;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

public interface FieldImageOrderRepo extends JpaRepository<FieldImageOrder, Long> {

  @Transactional
  @Modifying()
  @Query(nativeQuery = true, value = "update field_images set status = ?1, stats = ?2 where id = ?3")
  void updateStatusAndStats(String status, String stats, long id);

}