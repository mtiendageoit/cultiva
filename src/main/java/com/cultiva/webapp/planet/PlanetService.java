package com.cultiva.webapp.planet;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cultiva.webapp.field.*;
import com.cultiva.webapp.planet.orders.*;
import com.cultiva.webapp.security.UserPrincipal;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlanetService {

  private final OrderRepo orderRepo;
  private final FieldRepository fieldRepository;
  private final FieldOrderRepo fieldOrderRepo;

  public Optional<Order> orderBy(long fieldId, int fieldVersion, LocalDate imageDate) {
    return orderRepo.findByFieldIdAndFieldVersionAndImageDate(fieldId, fieldVersion, imageDate);
  }

  public OrdersCountAndField getOrdersCountAndFieldOrders(String fieldUuid, UserPrincipal principal) {
    OrdersCountAndField result = new OrdersCountAndField();
    if (fieldUuid != null) {
      Field field = fieldRepository.findByUuid(fieldUuid).get();
      List<Order> fieldOrders = orderRepo.findByFieldIdAndFieldVersion(field.getId(), field.getVersion());
      result.setFieldOrders(fieldOrders);
    }

    List<FieldOrder> orders = fieldOrderRepo.findByUserIdAndStatus(principal.getId(), OrderStatus.queued);

    List<OrderCount> count = orders.stream()
        .collect(Collectors.groupingBy(FieldOrder::getFieldUuid, Collectors.counting()))
        .entrySet().stream()
        .map(entry -> new OrderCount(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    result.setOrdersCount(count);

    return result;
  }

  public Order save(Order order) {
    return orderRepo.save(order);
  }

}
