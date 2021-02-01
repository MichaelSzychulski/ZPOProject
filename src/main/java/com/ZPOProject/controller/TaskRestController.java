package com.ZPOProject.controller;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Task;
import com.ZPOProject.service.ProjectService;
import com.ZPOProject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
public class TaskRestController {

    private TaskService taskService;
    private ProjectService projectService;

    @Autowired
    public TaskRestController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @PostMapping(path = "/tasks")
    ResponseEntity<Void> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.setTask(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(createdTask.getTaskId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/tasks/{projectId}")
    ResponseEntity<Void> createTask(@Valid @RequestBody Task task, @PathVariable Integer projectId) {
        Optional<Project> foundProject = projectService.getProject(projectId);
        task.setProject(foundProject.get());
        Task createdTask = taskService.setTask(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(createdTask.getTaskId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/tasks/{taskId}")
    ResponseEntity<Task> getTask(@PathVariable Integer taskId) {
        return ResponseEntity.of(taskService.getTask(taskId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Void> updateTask(@Valid @RequestBody Task task, @PathVariable Integer taskId) {
        return taskService.getTask(taskId).map(p -> {
            taskService.setTask(task);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId) {
        return taskService.getTask(taskId).map(p -> {
            taskService.deleteTask(taskId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/tasks")
    Page<Task> getTasks(Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @GetMapping(value = "/tasks_list")
    List<Task> getProjects() {
        return taskService.getTasks(PageRequest.of(0, 20)).getContent();
    }

}
