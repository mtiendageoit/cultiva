package com.cultiva.webapp.google;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatesRequest {
  private List<List<Double>> coords;
  private String from;
  private String to;
}
