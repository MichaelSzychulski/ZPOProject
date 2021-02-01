package com.ZPOProject.service;

import com.ZPOProject.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface ProjectService {

    Optional<Project> getProject(Integer projectId);
    Project setProject(Project project);
    void deleteProject(Integer projectId);
    Page<Project> getProjects(Pageable pageable);
    Page<Project> searchByName(String name, Pageable pageable);
    List<Project> findByStudent(Integer studentId);
}
