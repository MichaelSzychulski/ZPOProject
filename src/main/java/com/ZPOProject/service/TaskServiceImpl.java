package com.ZPOProject.service;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Task;
import com.ZPOProject.repository.ProjectRepository;
import com.ZPOProject.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Task> getTask(Integer taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Task setTask(Task task) {
        Task taskToSave = null;
        if (task.getTaskId() != null) {
            taskToSave = task;
        } else {
            taskToSave = new Task(task.getProject(),
                    task.getName(),
                    task.getTaskOrder(),
                    task.getDescription());
        }
        return taskRepository.save(taskToSave);
    }

    @Override
    @Transactional
    public void deleteTask(Integer taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public List<Task> getTasksByProject(Integer projectId){
        Optional<Project> opProject = projectRepository.findById(projectId);
        if(opProject.isPresent()) return taskRepository.findByProject(opProject.get());
        else return List.of();
    }

}
