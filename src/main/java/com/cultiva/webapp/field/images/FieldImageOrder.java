package com.cultiva.webapp.field.images;

import java.time.LocalDate;

import com.cultiva.webapp.planet.orders.OrderStatus;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fiel_images_queued")
public class FieldImageOrder {
  @Id
  @JsonIgnore
  private long id;

  private String uuid;
  private long fieldId;
  private int fieldVersion;
  private int indiceId;
  private long userId;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;
  private String stats;
  
  @Enumerated(EnumType.STRING)
  private FieldImageStatus status;

  private String geeProject;
  private String geeFolder;
  private String geeCollection;

  private String planetItemId;
  private String planetOrderId;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
}
