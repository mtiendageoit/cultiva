package com.cultiva.webapp.planet.orders;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.*;

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

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  private Date completedAt;

  private String fieldUuid;
  private String fieldName;
  private int fieldVersion;
  private String fieldBorderColor;

  @JsonIgnore
  private long userId;
}
