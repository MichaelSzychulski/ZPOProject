package com.ZPOProject.repository;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Student> findByIndexNum(String indexNum);

    Page<Student> findByIndexNumStartsWith(String indexNum, Pageable pageable);

    Page<Student> findBySurnameStartsWithIgnoreCase(String surname, Pageable pageable);

    @Query("SELECT s FROM Student s JOIN s.projects p WHERE p.projectId= :projectId")
    List<Student> findByProject(@Param("projectId")Integer projectId);

}
