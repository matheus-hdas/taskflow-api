package com.matheushdas.taskflowingapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matheushdas.taskflowingapi.model.utility.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_task")
@Data
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    public Task(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
        this.status = Status.OPEN.getValue();
    }

    public Task(UUID id, String name, String description, Project project) {
        this(name, description, project);
        this.id = id;
    }
}
