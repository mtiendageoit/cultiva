package com.cultiva.webapp.indices;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IndiceService {
  private final IndiceRepository indiceRepository;

  public List<Indice> indices() {
    return indiceRepository.findAll();
  }
}
