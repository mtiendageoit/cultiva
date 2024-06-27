package com.cultiva.webapp.tasks;

import java.time.LocalDate;

import com.cultiva.webapp.field.images.FieldImageStatus;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "view_user_tasks")
public class Task {
   @Id
  @JsonIgnore
  private long id;
  private String uuid;

  @JsonIgnore
  private long fieldId;

  @JsonIgnore
  private int fieldVersion;

  @JsonIgnore
  private int indiceId;

  @JsonIgnore
  private long userId;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;

  private String stats;

  @Enumerated(EnumType.STRING)
  private FieldImageStatus status;

  private String fieldUuid;
}
