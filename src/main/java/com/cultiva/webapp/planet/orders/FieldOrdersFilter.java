package com.cultiva.webapp.planet.orders;

import lombok.Data;

@Data
public class FieldOrdersFilter {
  private String fieldUuid;
  private OrderStatus status;
}
