package com.ZPOProject.service;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Task;
import com.ZPOProject.repository.ProjectRepository;
import com.ZPOProject.repository.StudentRepository;
import com.ZPOProject.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private StudentRepository studentRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              TaskRepository taskRepository,
                              StudentRepository studentRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<Project> getProject(Integer projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public Project setProject(Project project) {
        Project projectToSave = null;
        if (project.getProjectId() != null) {
            projectToSave = project;
        } else {
            projectToSave = new Project(project.getName(),
                    project.getDescription(),
                    project.getReturnDate());
        }
        return projectRepository.save(projectToSave);
    }

    @Override
    @Transactional
    public void deleteProject(Integer projectId) {
        for (Task task : taskRepository.findTasksForProject(projectId)) {
            taskRepository.delete(task);
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public Page<Project> getProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<Project> searchByName(String name, Pageable pageable) {
        return projectRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Project> findByStudent(Integer studentId) {
        return projectRepository.findByStudent(studentId);
    }

}
