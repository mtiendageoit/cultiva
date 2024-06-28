package com.cultiva.webapp.planet.orders;

import java.time.LocalDate;
import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepo extends JpaRepository<Order, Long> {
  Optional<Order> findByFieldIdAndFieldVersionAndImageDate(long fieldId, int fieldVersion, LocalDate imageDate);
  List<Order> findByFieldIdAndFieldVersion(long fieldId, int fieldVersion);
  List<Order> findByUserIdAndStatus(long userId, OrderStatus status);
  
  @Modifying
  @Transactional
  void deleteByUserId(long userId);
}
