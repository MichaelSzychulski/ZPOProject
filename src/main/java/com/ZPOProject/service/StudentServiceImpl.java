package com.ZPOProject.service;

import com.ZPOProject.models.Student;
import com.ZPOProject.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<Student> getStudent(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public Student setStudent(Student student) {
        Student studentToSave = null;
        if (student.getStudentId() != null) {
            studentToSave = student;
        } else {
            studentToSave = new Student(student.getName(),
                    student.getSurname(), student.getIndexNum(),
                    student.getEmail(), student.isLandline());
        }
        return studentRepository.save(studentToSave);
    }

    @Override
    public void deleteStudent(Integer studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public List<Student> getStudentsByProject(Integer projectId){
        return studentRepository.findByProject(projectId);
    }
}
