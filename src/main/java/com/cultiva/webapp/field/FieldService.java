package com.cultiva.webapp.field;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cultiva.webapp.exception.*;
import com.cultiva.webapp.field.images.*;
import com.cultiva.webapp.google.*;
import com.cultiva.webapp.indices.*;
import com.cultiva.webapp.planet.PlanetService;
import com.cultiva.webapp.planet.orders.*;
import com.cultiva.webapp.security.UserPrincipal;
import com.cultiva.webapp.utils.FakeData;

import lombok.AllArgsConstructor;

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
    googleCloudClient.deleteFieldImages(images);
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

  public FieldImageResult indiceImageField(String uuid, int indiceId, LocalDate from, UserPrincipal principal) {
    Field field = fieldByUuid(uuid);

    Optional<FieldImage> indiceImageField = fieldImageRepository.findByFieldIdAndFieldVersionAndIndiceIdAndImageDate(
        field.getId(),
        field.getVersion(), indiceId, from);
    if (indiceImageField.isPresent()) {
      System.out.println("=>Retornando imagen existente");
      System.out.println(indiceImageField.get());
      return new FieldImageResult(indiceImageField.get(), FieldImageStatus.READY);
    }

    Optional<Order> planetOrder = planetService.orderBy(field.getId(), field.getVersion(), from);

    if (planetOrder.isPresent()) {
      boolean isProcessing = planetOrder.get().getStatus() == OrderStatus.RUNNING;
      if (isProcessing) {
        System.out.println("=>La orden de la imagen se está processando, espere por favor");
        return new FieldImageResult(null, FieldImageStatus.PROCESSING_ORDER);
      }

      boolean isReady = planetOrder.get().getStatus() == OrderStatus.SUCCESS;
      if (isReady) {
        System.out.println("=>Procesando indice: " + indiceId);
        FieldImage image = processImageFieldForIndice(planetOrder.get(), field, indiceId, from, principal);
        return new FieldImageResult(image, FieldImageStatus.READY);
      }
    } else {
      System.out.println("=>Orden no existe, se envia a procesar orden y despues indice");
      Order order = processImageFieldOrder(field, from, principal);
      FieldImage image = processImageFieldForIndice(order, field, indiceId, from, principal);
      return new FieldImageResult(image, FieldImageStatus.READY);
    }

    throw new BaseException("no implementado");
  }

  private Order processImageFieldOrder(Field field, LocalDate from, UserPrincipal principal) {
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
        .status(OrderStatus.SUCCESS)
        .createdAt(new Date())
        .build();

    return planetService.save(order);
  }

  private String getGEEImageId(Order order, UserPrincipal principal) {
    // TODO: Change this
    String id = "projects/apgge-proyect/assets/" + order.getGeeFolder();
    id += "/" + order.getGeeCollection() + "/";
    id += order.getPlanetItemId();
    id += "_3B_AnalyticMS_SR_8b_clip_";
    id += order.getPlanetOrderId();
    return id;
  }

  @Transactional
  private FieldImage processImageFieldForIndice(Order order, Field field, int indiceId, LocalDate from,
      UserPrincipal principal) {
    String uuid = UUID.randomUUID().toString();
    Indice indice = indiceRepository.findById(indiceId).get();

    String geeImageId = getGEEImageId(order, principal);

    FieldImageStatistics statistics = googleCloudClient.processIndiceImageField(uuid, geeImageId, indice);

    FieldImage fieldImage = FieldImage.builder()
        .uuid(uuid)
        .fieldId(field.getId())
        .fieldVersion(field.getVersion())
        .indiceId(indiceId)
        .imageDate(from)
        .stats(statistics.toJsonString())
        .userId(principal.getId())
        .build();

    return fieldImageRepository.save(fieldImage);
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
