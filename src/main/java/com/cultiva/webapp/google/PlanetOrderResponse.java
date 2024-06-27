package com.cultiva.webapp.google;

import com.cultiva.webapp.planet.orders.OrderStatus;

import lombok.Data;

@Data
public class PlanetOrderResponse {
  private String code;
  private String message;
  private Order order;

  @Data
  public class Order {
    private String id;
    private String name;
    private String itemId;
    
    private String geeProject;
    private String geeFolder;
    private String geeCollection;
    private OrderStatus status;
  }
}