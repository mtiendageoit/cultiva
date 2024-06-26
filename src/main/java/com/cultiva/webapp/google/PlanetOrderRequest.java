package com.cultiva.webapp.google;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanetOrderRequest {
  private String from;
  private long userId;
  private long fieldId;
  private long fieldVersion;
  private List<List<Double>> coords;
}
