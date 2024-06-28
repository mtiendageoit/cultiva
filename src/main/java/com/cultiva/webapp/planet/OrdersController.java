package com.cultiva.webapp.planet;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.cultiva.webapp.planet.orders.OrdersCountAndField;
import com.cultiva.webapp.security.UserPrincipal;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OrdersController {
  private final PlanetService orderService;

  @GetMapping(value = "/orders/count-field")
  public OrdersCountAndField getOrdersCountAndFieldOrders(@RequestParam(required = false) String fieldUuid,
      @AuthenticationPrincipal UserPrincipal principal) {
    return orderService.getOrdersCountAndFieldOrders(fieldUuid, principal);
  }
}
