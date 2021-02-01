package com.ZPOProject.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @NotBlank(message = "Field 'name' cannot be empty.")
    @Size(min = 3, max = 50, message = "Name has to contains " +
            "from {min} to {max} characters.")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @OneToMany(mappedBy = "project")
    @JsonIgnoreProperties({"project"})
    private List<Task> tasks;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "project_student",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")})
    private List<Student> students;

    public Project(Integer projectId, String name, String description,
                   LocalDateTime creationDate, LocalDate returnDate) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.returnDate = returnDate;
    }

    public Project(String name, String description, LocalDate returnDate) {
        this.name = name;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", returnDate=" + returnDate +
                ", tasks=" + tasks +
                ", students=" + students +
                '}';
    }

    public String projectRepresentation() {
        return "Project " + name + "\n details: " + description + "\n created on: " +
                creationDate + "\n returned on: " + returnDate;
    }
}
