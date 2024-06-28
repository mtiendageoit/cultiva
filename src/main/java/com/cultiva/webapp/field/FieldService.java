package com.cultiva.webapp.field;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cultiva.webapp.exception.*;
import com.cultiva.webapp.field.images.*;
import com.cultiva.webapp.google.*;
import com.cultiva.webapp.indices.Indice;
import com.cultiva.webapp.indices.IndiceRepository;
import com.cultiva.webapp.planet.PlanetService;
import com.cultiva.webapp.planet.orders.*;
import com.cultiva.webapp.security.UserPrincipal;
import com.cultiva.webapp.utils.FakeData;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FieldService {
  private final GoogleCloudClient googleCloudClient;

  private final FieldRepository repository;
  private final IndiceRepository indiceRepository;
  private final FieldImageRepository fieldImageRepository;

  private final PlanetService planetService;

  public Field create(FieldDto input, UserPrincipal principal) {
    Field field = Field.builder()
        .uuid(UUID.randomUUID().toString())
        .name(input.getName())
        .userId(principal.getId())
        .cropId(input.getCropId())
        .plantingDate(input.getPlantingDate())
        .harvestDate(input.getHarvestDate())
        .borderColor(input.getBorderColor())
        .borderSize(input.getBorderSize())
        .wkt(input.getWkt())
        .version(1)
        .createdAt(new Date())
        .build();

    return repository.save(field);
  }

  public List<Field> getAll(UserPrincipal principal) {
    return repository.findAllByUserId(principal.getId());
  }

  @Transactional
  public void delete(String uuid) {
    Field field = fieldByUuid(uuid);
    deleteFieldImages(fieldImageRepository.findByFieldId(field.getId()));
    repository.delete(field);
  }

  @Transactional
  public Field putGeometry(String uuid, FieldDto input) {
    Field field = fieldByUuid(uuid);
    field.setWkt(input.getWkt());
    field.setVersion(field.getVersion() + 1);// Change version field when geometry change
    field = repository.save(field);

    // Delete field indices images for update field geometry
    deleteFieldImages(fieldImageRepository.findByFieldId(field.getId()));

    return field;
  }

  private void deleteFieldImages(List<FieldImage> images) {
    fieldImageRepository.deleteAll(images);
    // googleCloudClient.deleteFieldImages(images);
  }

  public Field update(String uuid, FieldDto input) {
    Field field = fieldByUuid(uuid);

    field.setName(input.getName());
    field.setCropId(input.getCropId());
    field.setPlantingDate(input.getPlantingDate());
    field.setHarvestDate(input.getHarvestDate());
    field.setBorderColor(input.getBorderColor());
    field.setBorderSize(input.getBorderSize());

    return repository.save(field);
  }

  private Field fieldByUuid(String uuid) {
    return repository.findByUuid(uuid)
        .orElseThrow(() -> new NotFoundException("field-not-found", "Field not found"));
  }

  @Transactional
  public FieldImage indiceImageField(String fieldUuid, int indiceId, LocalDate from, UserPrincipal principal) {
    Field field = fieldByUuid(fieldUuid);

    Optional<FieldImage> indiceImageField = fieldImageRepository.findByFieldIdAndFieldVersionAndIndiceIdAndImageDate(
        field.getId(), field.getVersion(), indiceId, from);

    boolean processed = indiceImageField.isPresent() && (indiceImageField.get().getStatus() == FieldImageStatus.success
        || indiceImageField.get().getStatus() == FieldImageStatus.failed);
    if (processed) {
      return indiceImageField.get();
    }

    String fieldImageUuid = UUID.randomUUID().toString();

    Order order;
    Optional<Order> planetOrder = planetService.orderBy(field.getId(), field.getVersion(), from);
    if (planetOrder.isEmpty()) {
      order = createImageFieldOrder(field, from, principal);
      return createFieldImage(fieldImageUuid, field, indiceId, from, FieldImageStatus.valueOf(order.getStatus().name()),
          principal.getId());
    }

    order = planetOrder.get();

    boolean failed = order.getStatus() == OrderStatus.failed || order.getStatus() == OrderStatus.partial
        || order.getStatus() == OrderStatus.cancelled;
    if (failed) {
      if (indiceImageField.isEmpty()) {
        return createFieldImage(fieldImageUuid, field, indiceId, from, FieldImageStatus.failed, principal.getId());
      } else {
        indiceImageField.get().setStatus(FieldImageStatus.failed);
        return fieldImageRepository.save(indiceImageField.get());
      }
    }

    if (order.getStatus() == OrderStatus.queued || order.getStatus() == OrderStatus.running) {
      if (indiceImageField.isEmpty()) {
        return createFieldImage(fieldImageUuid, field, indiceId, from, FieldImageStatus.queued, principal.getId());
      } else {
        return indiceImageField.get();
      }
    }

    // Order Success

    Indice indice = indiceRepository.findById(indiceId)
        .orElseThrow(() -> new NotFoundException("indice-not-exists", "The indice id not exists"));

    String geeImage = getGEEImageId(order);

    FieldImage image;
    if (indiceImageField.isPresent()) {
      image = indiceImageField.get();
      fieldImageUuid = image.getUuid();
    } else {
      image = FieldImage.builder()
          .uuid(fieldImageUuid)
          .fieldId(field.getId())
          .fieldVersion(field.getVersion())
          .indiceId(indiceId)
          .imageDate(from)
          .userId(principal.getId())
          .build();
    }

    try {
      FieldImageStatistics stats = googleCloudClient.processIndiceImageField(fieldImageUuid, geeImage, indice);
      if (stats != null) {
        image.setStats(stats.toJsonString());
      }
      image.setStatus(FieldImageStatus.success);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      image.setStatus(FieldImageStatus.failed);
    }

    return fieldImageRepository.save(image);
  }

  private FieldImage createFieldImage(String uuid, Field field, int indiceId, LocalDate from, FieldImageStatus status,
      long userId) {
    FieldImage image = FieldImage.builder()
        .uuid(uuid)
        .fieldId(field.getId())
        .fieldVersion(field.getVersion())
        .indiceId(indiceId)
        .imageDate(from)
        .status(status)
        .userId(userId)
        .build();
    return fieldImageRepository.save(image);
  }

  private Order createImageFieldOrder(Field field, LocalDate from, UserPrincipal principal) {
    PlanetOrderResponse response = googleCloudClient.processPlanetOrder(field, from);

    if (response.getCode().equalsIgnoreCase("PlanetImagesUnavailable")) {
      throw new BaseException(response.getCode(), response.getMessage());
    }

    Order order = Order.builder()
        .userId(principal.getId())
        .fieldId(field.getId())
        .fieldVersion(field.getVersion())
        .imageDate(from)
        .geeProject(response.getOrder().getGeeProject())
        .geeFolder(response.getOrder().getGeeFolder())
        .geeCollection(response.getOrder().getGeeCollection())
        .planetItemId(response.getOrder().getItemId())
        .planetOrderId(response.getOrder().getId())
        .planetOrderName(response.getOrder().getName())
        .status(response.getOrder().getStatus())
        .createdAt(new Date())
        .build();

    return planetService.save(order);
  }

  private String getGEEImageId(Order order) {
    String id = "projects/" + order.getGeeProject() + "/assets/" + order.getGeeFolder();
    id += "/" + order.getGeeCollection() + "/";
    id += order.getPlanetItemId();
    id += "_3B_AnalyticMS_SR_8b_clip_";
    id += order.getPlanetOrderId();
    return id;
  }

  public List<FieldImageDateDto> fieldImages(String uuid) {
    // Field field = fieldByUuid(uuid);

    // LocalDate to = LocalDate.now().plusDays(1);
    // LocalDate from = to.minusYears(1);

    // List<FieldImageDateDto> images = googleCloudClient.imageDates(field, from,
    // to);
    // Collections.sort(images,
    // Comparator.comparing(FieldImageDateDto::getImageDate).reversed());

    return FakeData.fakeSatelliteImageDates();
  }

  public void deleteFieldsFor(long userId) {
    deleteFieldImages(fieldImageRepository.findByUserId(userId));
    repository.deleteByUserId(userId);
  }

}
