package com.cultiva.webapp.tasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cultiva.webapp.field.images.FieldImageStatus;

public interface TasksRepository extends JpaRepository<Task, Long> {
  List<Task> findByUserIdAndStatus(long userId, FieldImageStatus status);
}
