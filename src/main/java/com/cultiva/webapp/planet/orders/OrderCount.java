package com.cultiva.webapp.planet.orders;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCount {
  private String fieldUuid;
  private long count;
}
