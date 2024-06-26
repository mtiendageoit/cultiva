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
  private Long userId;
  private Long fieldId;
  private Integer fieldVersion;
  
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private String geeProject;
  private String geeFolder;
  private String geeCollection;

  private String planetItemId;
  private String planetOrderId;
  private String planetOrderName;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
}
