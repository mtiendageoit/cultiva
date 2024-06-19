package com.cultiva.webapp.crop;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "crops")
public class Crop {
  @Id
  private Integer id;
  private String name;
}
