package com.cultiva.webapp.planet.orders;

import java.util.List;

import lombok.Data;

@Data
public class OrdersCountAndField {
  private List<Order> fieldOrders;
  private List<OrderCount> ordersCount;
}
