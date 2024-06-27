package com.cultiva.webapp.tasks;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cultiva.webapp.security.UserPrincipal;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tasks")
public class TasksController {
  private final TaskService taskService;

  @GetMapping("/count")
  public List<FieldTaskCount> getTasksCount(@AuthenticationPrincipal UserPrincipal principal) {
    return taskService.userTasksCount(principal);
  }

}
