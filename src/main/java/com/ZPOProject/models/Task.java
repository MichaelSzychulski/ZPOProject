package com.ZPOProject.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @JsonIgnoreProperties("tasks")
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "task_order")
    private Integer taskOrder;

    @Column(length = 1000)
    private String description;

    @Column(name = "datetime_add", nullable = false)
    private LocalDateTime addedDate;

    public Task(Project project, String name, Integer taskOrder, String description) {
        this.project = project;
        this.name = name;
        this.taskOrder = taskOrder;
        this.description = description;
        this.addedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", project=" + project.projectRepresentation() +
                ", name='" + name + '\'' +
                ", taskOrder=" + taskOrder +
                ", description='" + description + '\'' +
                ", addedDate=" + addedDate +
                '}';
    }
}
