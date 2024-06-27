package com.cultiva.webapp.field.images;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cultiva.webapp.google.GoogleCloudClient;
import com.cultiva.webapp.indices.*;
import com.cultiva.webapp.planet.orders.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FieldImageTask {
  private IndiceService indiceService;
  private final FieldImageOrderRepo fieldImageOrderRepo;

  private final GoogleCloudClient googleCloudClient;

  @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
  public void processIndiceImageField() throws InterruptedException {
    List<FieldImageOrder> items = fieldImageOrderRepo.findAll();

    if (items.size() > 0) {
      List<Indice> indices = indiceService.indices();

      for (FieldImageOrder item : items) {
        if (item.getOrderStatus() == OrderStatus.queued || item.getOrderStatus() == OrderStatus.running)
          continue;

        if (item.getOrderStatus() == OrderStatus.success) {
          item = processIndiceFor(item, indices);
        } else { // partial, failed, cancelled
          item.setStatus(FieldImageStatus.failed);
        }
        fieldImageOrderRepo.updateStatusAndStats(item.getStatus().name(), item.getStats(), item.getId());
      }
    }
  }

  private FieldImageOrder processIndiceFor(FieldImageOrder field, List<Indice> indices) {
    try {
      Indice indice = indices.stream().filter(i -> i.getId() == field.getIndiceId()).findFirst().get();
      FieldImageStatistics stats = googleCloudClient.processIndiceImageField(field.getUuid(), getGEEImageId(field),
          indice);
      field.setStats(stats.toJsonString());
      field.setStatus(FieldImageStatus.success);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      log.error("FieldImageOrder: {}", field);
      field.setStatus(FieldImageStatus.failed);
    }
    return field;
  }

  private String getGEEImageId(FieldImageOrder imageOrder) {
    String id = "projects/" + imageOrder.getGeeProject() + "/assets/" + imageOrder.getGeeFolder();
    id += "/" + imageOrder.getGeeCollection() + "/";
    id += imageOrder.getPlanetItemId();
    id += "_3B_AnalyticMS_SR_8b_clip_";
    id += imageOrder.getPlanetOrderId();
    return id;
  }
}
