package com.cultiva.webapp.google;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cultiva.webapp.config.AppConfig;
import com.cultiva.webapp.exception.BaseException;
import com.cultiva.webapp.field.Field;
import com.cultiva.webapp.field.images.*;
import com.cultiva.webapp.indices.Indice;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GoogleCloudClient {
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final AppConfig config;
  private final RestTemplate restTemplate;

  public FieldImageStatistics processIndiceImageField(String imageName, String geeImageId, Indice indice) {

    ImageRequest requestObject = ImageRequest.builder()
        .imageName(imageName)
        .geeImageId(geeImageId)
        .build();

    try {
      ResponseEntity<FieldImageStatistics> response = restTemplate.postForEntity(indice.getUrl(), requestObject,
          FieldImageStatistics.class);
      return response.getBody();
    } catch (Exception e) {
      e.getMessage();
      System.err.println(e.getMessage());
    }

    return null;
  }

  public PlanetOrderResponse processPlanetOrder(Field field, LocalDate from) {
    List<List<Double>> coords = coordinatesFromWKT(field.getWkt());

    PlanetOrderRequest request = PlanetOrderRequest.builder()
        .from(DATE_FORMAT.format(from))
        .userId(field.getUserId())
        .fieldId(field.getId())
        .fieldVersion(field.getVersion())
        .coords(coords)
        .build();

    try {
      String url = config.getGoogleCloudFunctionsUrl();
      ResponseEntity<PlanetOrderResponse> response = restTemplate.postForEntity(url, request,
          PlanetOrderResponse.class);
      return response.getBody();
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new BaseException("order-error", e.getMessage());
    }
  }

  private List<List<Double>> coordinatesFromWKT(String wkt) {
    String coords = wkt.replace("POLYGON((", "").replace("))", "");
    String pairsCords[] = coords.split(",");

    List<List<Double>> coordinates = new ArrayList<>();
    String lonlat[];
    for (String coord : pairsCords) {
      lonlat = coord.split(" ");
      coordinates.add(Arrays.asList(Double.valueOf(lonlat[0]), Double.valueOf(lonlat[1])));
    }

    return coordinates;
  }

  @Async
  public void deleteFieldImages(List<FieldImage> images) {
    List<String> uuids = images.stream().map(item -> item.getUuid()).toList();
    DeleteImagesRequest fieldImages = new DeleteImagesRequest(uuids);

    String url = config.getGoogleCloudFunctionsUrl() + "/storage-delete-images";
    restTemplate.postForEntity(url, fieldImages, String.class);
  }

  public void updateUserAvatar(MultipartFile file, String filename) {
    String url = config.getGoogleCloudFunctionsUrl() + "/user-avatar";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", file.getResource());
    body.add("filename", filename);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    restTemplate.postForEntity(url, requestEntity, String.class);
  }
}
