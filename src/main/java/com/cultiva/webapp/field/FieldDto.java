package com.cultiva.webapp.field;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FieldDto {
  private String name;

  private Integer cropId;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date plantingDate;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date harvestDate;

  private String borderColor;

  @Min(1)
  private Integer borderSize;

  private String wkt;
}