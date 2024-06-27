package com.cultiva.webapp.tasks;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cultiva.webapp.field.images.FieldImageStatus;
import com.cultiva.webapp.security.UserPrincipal;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskService {
  private final TasksRepository tasksRepository;

  public List<FieldTaskCount> userTasksCount(UserPrincipal principal) {
    List<Task> tasks = tasksRepository.findByUserIdAndStatus(principal.getId(), FieldImageStatus.queued);

    List<FieldTaskCount> tasksCount = tasks.stream()
        .collect(Collectors.groupingBy(Task::getFieldUuid, Collectors.counting()))
        .entrySet().stream()
        .map(entry -> new FieldTaskCount(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    return tasksCount;
  }
}
