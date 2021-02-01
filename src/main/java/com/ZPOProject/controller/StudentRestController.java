package com.ZPOProject.controller;

import com.ZPOProject.models.Student;
import com.ZPOProject.service.StudentService;
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

@RestController
@RequestMapping(value = "/api")
public class StudentRestController {

    private StudentService studentService;

    @Autowired
    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping(path = "/students")
    ResponseEntity<Void> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentService.setStudent(student);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{studentId}")
                .buildAndExpand(createdStudent.getStudentId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/students/{studentId}")
    ResponseEntity<Student> getStudent(@PathVariable Integer studentId) {
        return ResponseEntity.of(studentService.getStudent(studentId));
    }

    @PutMapping("/students/{studentId}")
    public ResponseEntity<Void> updateStudent(@Valid @RequestBody Student student, @PathVariable Integer studentId) {
        return studentService.getStudent(studentId).map(p -> {
            studentService.setStudent(student);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer studentId) {
        return studentService.getStudent(studentId).map(p -> {
            studentService.deleteStudent(studentId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/students")
    Page<Student> getTasks(Pageable pageable) {
        return studentService.getStudents(pageable);
    }

    @GetMapping(value = "/students_list")
    List<Student> getProjects() {
        return studentService.getStudents(PageRequest.of(0,20)).getContent();
    }
}
