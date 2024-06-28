package com.cultiva.webapp.planet.orders;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  private Long userId;

  @JsonIgnore
  private Long fieldId;

  private Integer fieldVersion;
  
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @JsonIgnore
  private String geeProject;

  @JsonIgnore
  private String geeFolder;

  @JsonIgnore
  private String geeCollection;

  @JsonIgnore
  private String planetItemId;

  @JsonIgnore
  private String planetOrderId;

  @JsonIgnore
  private String planetOrderName;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
}
