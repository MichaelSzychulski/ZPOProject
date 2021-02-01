package com.ZPOProject.service;

import com.ZPOProject.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Optional<Task> getTask(Integer taskId);
    Task setTask(Task task);
    void deleteTask(Integer taskId);
    Page<Task> getTasks(Pageable pageable);
    List<Task> getTasksByProject(Integer projectId);
}
