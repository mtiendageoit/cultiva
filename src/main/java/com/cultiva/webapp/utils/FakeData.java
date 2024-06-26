package com.cultiva.webapp.utils;

import java.time.LocalDate;
import java.util.*;

import com.cultiva.webapp.field.images.FieldImageDateDto;

public class FakeData {

  public static List<FieldImageDateDto> fakeSatelliteImageDates() {
    List<FieldImageDateDto> list = new ArrayList<>();
    LocalDate fecha = LocalDate.now();

    for (int i = 0; i < 365; i++) {
      LocalDate date = fecha.minusDays(i);
      double cloudyPercentage = 100;
      FieldImageDateDto dto = new FieldImageDateDto(cloudyPercentage, date);
      list.add(dto);
    }
    return list;
  }
}
