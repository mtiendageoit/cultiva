package com.cultiva.webapp.tasks;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldTaskCount {
  private String fieldUuid;
  private long tasksCount;
}
