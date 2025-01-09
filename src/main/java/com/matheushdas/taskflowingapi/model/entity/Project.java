package com.matheushdas.taskflowingapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matheushdas.taskflowingapi.model.utility.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_project")
@Data
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private String status;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<Task> tasks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.IN_PROGRESS.getValue();
    }

    public Project(UUID id, String name, String description) {
        this(name, description);
        this.id = id;
    }
}
