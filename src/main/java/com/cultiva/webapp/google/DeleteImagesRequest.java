package com.cultiva.webapp.google;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
public class DeleteImagesRequest {
  private List<String> images;
}
