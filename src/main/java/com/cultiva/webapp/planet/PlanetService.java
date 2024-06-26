package com.cultiva.webapp.planet;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cultiva.webapp.planet.orders.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlanetService {

  private final OrderRepo orderRepo;

  public Optional<Order> orderBy(long fieldId, int fieldVersion, LocalDate imageDate) {
    return orderRepo.findByFieldIdAndFieldVersionAndImageDate(fieldId, fieldVersion, imageDate);
  }

  public Order save(Order order) {
    return orderRepo.save(order);
  }

}
