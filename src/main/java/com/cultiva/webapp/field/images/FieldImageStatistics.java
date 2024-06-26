package com.cultiva.webapp.field.images;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class FieldImageStatistics {
  private Double max;
  private Double mean;
  private Double median;
  private Double min;
  private Double p25;
  private Double p50;
  private Double p75;
  private Double stdDev;
  private Double variance;

  public String toJsonString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }
}
