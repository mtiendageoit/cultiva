package com.cultiva.webapp.google;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
  private String imageName;
  private String geeImageId;
}
