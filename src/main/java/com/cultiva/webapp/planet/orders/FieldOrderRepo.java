package com.cultiva.webapp.planet.orders;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldOrderRepo extends JpaRepository<FieldOrder, String> {
  List<FieldOrder> findByUserIdAndStatus(long userId, OrderStatus status);
}
