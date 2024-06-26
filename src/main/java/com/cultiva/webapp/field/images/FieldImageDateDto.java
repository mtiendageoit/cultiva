package com.cultiva.webapp.field.images;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldImageDateDto {
  private double cloudyPercentage;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate imageDate;
}
