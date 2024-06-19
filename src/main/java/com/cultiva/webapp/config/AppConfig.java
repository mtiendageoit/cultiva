package com.cultiva.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

  @Value("${google.cloud.functions.url}")
  private String googleCloudFunctionsUrl;

  @Value("${google.cloud.storage.images.url}")
  private String googleCloudStorageImagesUrl;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
