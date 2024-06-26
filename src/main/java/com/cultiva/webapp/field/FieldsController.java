package com.cultiva.webapp.field;

import java.time.LocalDate;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.cultiva.webapp.field.images.*;
import com.cultiva.webapp.security.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/fields")
public class FieldsController {

  private final FieldService service;

  @PostMapping
  public Field create(@Valid @RequestBody FieldDto input, @AuthenticationPrincipal UserPrincipal principal) {
    return service.create(input, principal);
  }

  @PutMapping("/{uuid}")
  public Field update(@PathVariable String uuid, @RequestBody FieldDto input) {
    return service.update(uuid, input);
  }

  @GetMapping
  public List<Field> getAll(@AuthenticationPrincipal UserPrincipal principal) {
    return service.getAll(principal);
  }

  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @DeleteMapping("/{uuid}")
  public void delete(@PathVariable String uuid) {
    service.delete(uuid);
  }

  @PutMapping("/{uuid}/geometry")
  public Field putGeometry(@PathVariable String uuid, @RequestBody FieldDto input) {
    return service.putGeometry(uuid, input);
  }

  @GetMapping("/{uuid}/images-dates")
  public List<FieldImageDateDto> fieldImages(@PathVariable String uuid) {
    return service.fieldImages(uuid);
  }

  @PostMapping("/{uuid}/image")
  public FieldImageResult indiceImageField(@PathVariable String uuid, @RequestParam int indice,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate from,
      @AuthenticationPrincipal UserPrincipal principal) {
    return service.indiceImageField(uuid, indice, from, principal);
  }
}
