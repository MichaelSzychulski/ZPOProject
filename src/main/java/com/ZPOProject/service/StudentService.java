package com.ZPOProject.service;

import com.ZPOProject.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Optional<Student> getStudent(Integer studentId);
    Student setStudent(Student student);
    void deleteStudent(Integer studentId);
    Page<Student> getStudents(Pageable pageable);
    List<Student> getStudentsByProject(Integer projectId);
}
