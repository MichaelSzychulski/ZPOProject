package com.ZPOProject.repository;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

        Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

        List<Project> findByNameContainingIgnoreCase(String name);

        @Query("SELECT z FROM Project z JOIN z.students s WHERE s.studentId= :studentId")
        List<Project> findByStudent(@Param("studentId")Integer studentId);

}
