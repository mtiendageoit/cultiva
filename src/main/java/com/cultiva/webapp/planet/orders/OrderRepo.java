package com.cultiva.webapp.planet.orders;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
  Optional<Order> findByFieldIdAndFieldVersionAndImageDate(long fieldId, int fieldVersion, LocalDate imageDate);
}
