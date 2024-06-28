package com.cultiva.webapp.planet.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "view_field_orders")
public class FieldOrder {
  @Id
  @JsonIgnore
  private long id;

  @JsonIgnore
  private long userId;
  private String fieldUuid;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;
}
