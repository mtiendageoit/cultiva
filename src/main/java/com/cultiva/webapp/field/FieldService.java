package com.cultiva.webapp.field;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cultiva.webapp.exception.NotFoundException;
import com.cultiva.webapp.field.images.*;
import com.cultiva.webapp.google.GoogleCloudClient;
import com.cultiva.webapp.indices.*;
import com.cultiva.webapp.security.UserPrincipal;
import com.cultiva.webapp.utils.RandomCodeGenerator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FieldService {
  private final GoogleCloudClient googleCloudClient;

  private final FieldRepository repository;
  private final IndiceRepository indiceRepository;
  private final FieldImageRepository fieldImageRepository;

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

  @Transactional
  public FieldImage indiceImageField(String uuid, int indiceId, LocalDate from, UserPrincipal principal) {
    Field field = fieldByUuid(uuid);
    Optional<FieldImage> opt = fieldImageRepository.findByFieldIdAndFieldVersionAndIndiceIdAndImageDate(field.getId(),
        field.getVersion(), indiceId, from);
    if (opt.isPresent()) {
      return opt.get();
    }

    Indice indice = indiceRepository.findById(indiceId)
        .orElseThrow(() -> new NotFoundException("indice-not-exists", "The indice id not exists"));

    String imageUuid = RandomCodeGenerator.generateUUIDCode();
    FieldImageStatistics stats = googleCloudClient.processIndiceImageField(field, imageUuid, indice, from);

    FieldImage fieldImage = FieldImage.builder()
        .uuid(imageUuid)
        .fieldId(field.getId())
        .indiceId(indiceId)
        .userId(principal.getId())
        .imageDate(from)
        .fieldVersion(field.getVersion())
        .stats(stats.toJsonString())
        .build();

    fieldImage = fieldImageRepository.save(fieldImage);

    return fieldImage;
  }

  public List<FieldImageDateDto> fieldImages(String uuid) {
    Field field = fieldByUuid(uuid);

    LocalDate to = LocalDate.now().plusDays(1);
    LocalDate from = to.minusYears(1);

    List<FieldImageDateDto> images = googleCloudClient.imageDates(field, from, to);
    Collections.sort(images, Comparator.comparing(FieldImageDateDto::getImageDate).reversed());
    return images;
  }

  public void deleteFieldsFor(long userId) {
    deleteFieldImages(fieldImageRepository.findByUserId(userId));
    repository.deleteByUserId(userId);
  }

}
