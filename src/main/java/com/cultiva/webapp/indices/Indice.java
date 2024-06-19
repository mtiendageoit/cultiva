package com.cultiva.webapp.indices;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "indices")
public class Indice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private String description;
  private String url;
}
