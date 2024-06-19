package com.cultiva.webapp.field.images;

import java.util.Date;

import lombok.Data;

@Data
public class FieldImageDto {
  private Date from;
  private int indice;
  private String coords;
}
