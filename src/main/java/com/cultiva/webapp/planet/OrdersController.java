package com.cultiva.webapp.planet;

import java.io.IOException;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.cultiva.webapp.dto.PageResult;
import com.cultiva.webapp.google.auth.GCPAuthentication;
import com.cultiva.webapp.planet.orders.*;
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

  @GetMapping("/orders/filter")
  public PageResult<FieldOrder> fieldOrdersBy(FieldOrdersFilter filter, @PageableDefault Pageable pageable,
      @RequestParam int draw, @AuthenticationPrincipal UserPrincipal principal) {

    Page<FieldOrder> orders = orderService.fieldOrdersBy(filter, principal, pageable);

    PageResult<FieldOrder> page = new PageResult<>();
    page.setDraw(draw);
    page.setRecordsTotal(orders.getTotalElements());
    page.setRecordsFiltered(orders.getTotalElements());
    page.setData(orders.getContent());

    return page;
  }

  @GetMapping("/subscriptions")
  public String key() {
    String url = "https://us-south1-code-cultiva.cloudfunctions.net/planet-subscription-create";

    try {
      return GCPAuthentication.getIdTokenFromMetadataServer(url);
    } catch (Exception e) {
      return e.toString();
    }

  }
}
