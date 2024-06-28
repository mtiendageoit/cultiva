package com.cultiva.webapp.planet.orders;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldOrderRepo extends JpaRepository<FieldOrder, String> {
  Page<FieldOrder> findByUserId(Long userId, Pageable pageable);
  List<FieldOrder> findByUserIdAndStatus(long userId, OrderStatus status);
  Page<FieldOrder> findByUserIdAndStatus(long userId, OrderStatus status, Pageable pageable);
  Page<FieldOrder> findByFieldUuidAndUserId(String fieldUuid, Long userId, Pageable pageable);
  Page<FieldOrder> findByFieldUuidAndStatusAndUserId(String fieldUuid, OrderStatus status, Long userId,
      Pageable pageable);
}
