package com.ZPOProject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student", indexes = {@Index(name = "idx_surname",
            columnList = "surname", unique = false),
        @Index(name = "idx_index_num", columnList = "index_num",
                unique = false)})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(name = "index_num", nullable = false, unique = true)
    private String indexNum;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    private boolean landline;

    @ManyToMany(mappedBy = "students")
    private List<Project> projects;

    public Student(String name, String surname, String indexNum,
                   String email, boolean landline) {
        this.name = name;
        this.surname = surname;
        this.indexNum = indexNum;
        this.email = email;
        this.landline = landline;
    }

}
