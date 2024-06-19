package com.cultiva.webapp.crop;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CropService {
  private CropRepository repository;

  public List<Crop> crops() {
    return repository.findAll(Sort.by("name"));
  }
}
