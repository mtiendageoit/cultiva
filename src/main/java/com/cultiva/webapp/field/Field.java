package com.cultiva.webapp.field;

import java.util.Date;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fields")
public class Field {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;
  private String name;

  @JsonIgnore
  private long userId;
  private Integer cropId;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date plantingDate;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date harvestDate;

  private String borderColor;
  private int borderSize;
  private String wkt;

  private int version;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
}
